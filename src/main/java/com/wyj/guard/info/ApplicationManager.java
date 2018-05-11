package com.wyj.guard.info;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.event.ApplicationEvent;
import com.wyj.guard.context.event.ApplicationListener;
import com.wyj.guard.context.event.CloseInstanceEvent;
import com.wyj.guard.context.event.RefreshEvent;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.share.Closeable;
import com.wyj.guard.share.Pair;
import com.wyj.guard.share.enums.InstanceStatus;
import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.utils.DateTimeUtils;
import com.wyj.guard.utils.ThreadPoolUtils;
import com.wyj.guard.web.InstanceCondition;
import com.wyj.guard.web.InstanceEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class ApplicationManager implements Closeable,
        ApplicationListener<ApplicationEvent>, InstanceEndpoint {

    private Logger logger = LoggerFactory.getLogger(ApplicationManager.class);

    // 上下文
    protected ConfigurableGuardContext context;

    // 应用配置和应用实体的聚合
    private AtomicReference<Pair<ApplicationConfig, ApplicationInfo>> applicationPairReference;

    // 所有实例管理器的Map（key为InstanceId）
    private Map<String, InstanceManager> instanceManagerMap = new ConcurrentHashMap<>();

    // 所有的实例管理器
    protected List<InstanceManager> instanceManagerList = new ArrayList<>();

    // 已启动的实例
    private List<String> startedInstances = new ArrayList<>();

    // 未启动的实例
    private List<String> notStartedInstances = new ArrayList<>();

    // 任务调度器
    private ScheduledExecutorService scheduled;

    // 执行任务线程池
    private ExecutorService taskExecutor;

    private volatile boolean virtualClosed = true;

    private volatile boolean physicalClosed = true;

    public ApplicationManager(ConfigurableGuardContext context,
                              ApplicationConfig applicationConfig) {
        this.context = context;
        this.applicationPairReference = new AtomicReference<>(Pair.newPair(applicationConfig,
                context.getApplicationInfoSupplier().apply(applicationConfig)));

        // 加载该应用所有的实例
        List<InstanceConfig> instanceConfigs = Arrays.asList(context.getInstanceConfigLoader()
                .load(applicationConfig.getApplicationName()));

        // 生成实例管理器
        for (InstanceConfig instanceConfig : instanceConfigs) {
            InstanceManager instanceManager = new InstanceManager(context,
                    this, instanceConfig);
            instanceManagerMap.put(instanceManager.getInstanceInfo().getInstanceId(),
                    instanceManager);
            instanceManagerList.add(instanceManager);
        }
        // 添加应用监听器
        context.addApplicationListener(this);
    }

    public boolean launch() {
        // 初始化调度器
        if (scheduled == null || scheduled.isShutdown()) {
            scheduled = Executors.newSingleThreadScheduledExecutor(
                    new DaemonThreadFactory(getApplicationInfo().getApplicationName() +
                            "-Application-Scheduled-ThreadPool"));
        }
        // 初始化任务执行的线程池
        if (taskExecutor == null || taskExecutor.isShutdown()) {
            taskExecutor = Executors.newSingleThreadExecutor(
                    new DaemonThreadFactory(getApplicationInfo().getApplicationName() +
                            "-Application-Exec-Task-ThreadPool"));
        }
        return CompletableFuture.supplyAsync(() -> {
            startedInstances.clear();
            notStartedInstances.clear();
            // 获取已经启动的实例
            instanceManagerList.forEach(instanceManager -> {
                InstanceInfo instanceInfo = instanceManager.getInstanceInfo();
                // 不允许启动的实例
                if (instanceInfo.getStatus().equals(LaunchStatus.SHUTDOWN)) {
                    // 放入未启动实例列表中
                    notStartedInstances.add(instanceInfo.getInstanceId());
                    return;
                }
                // 初始化已启动的实例
                InstanceStatus instanceStatus = instanceManager.getInstanceStatus();
                if (instanceStatus == InstanceStatus.UP ||
                        instanceStatus == InstanceStatus.DOWN) {
                    instanceManager.launch();
                    startedInstances.add(instanceManager.getInstanceInfo().getInstanceId());
                    return;
                }
                // 放入未启动实例列表中
                notStartedInstances.add(instanceInfo.getInstanceId());
            });
            virtualClosed = false;
            physicalClosed = false;
            // 启动监控
            scheduled.schedule(new DefendInstancesTask(), 0, TimeUnit.MILLISECONDS);
            return true;
        }, taskExecutor).exceptionally(throwable -> {
            logger.warn("{}应用启动出错!", getApplicationInfo().getApplicationName(), throwable);
            return false;
        }).join();
    }

    @Override
    public boolean selfClose() {
        instanceManagerList.forEach(instanceManager -> instanceManager.selfClose());
        ThreadPoolUtils.shutdown(scheduled);
        ThreadPoolUtils.shutdown(taskExecutor);
        scheduled = null;
        taskExecutor = null;
        context.removeApplicationListener(this);
        return true;
    }

    @Override
    public boolean virtualClose() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("{} 应用虚拟关闭...", getApplicationInfo().getApplicationName());
            for (InstanceManager instanceManager : instanceManagerList) {
                boolean instanceClose = instanceManager.virtualClose();
                if (instanceClose) {
                    // 关闭成功
                    handleCloseSuccessful(instanceManager.getInstanceInfo());
                } else {
                    // 关闭失败
                    logger.info("{} 应用虚拟关闭失败", getApplicationInfo().getApplicationName());
                    return false;
                }
            }
            virtualClosed = true;
            logger.info("{} 应用虚拟关闭成功", getApplicationInfo().getApplicationName());
            return virtualClosed;
        }, taskExecutor).exceptionally(throwable -> {
            logger.warn("{} 应用虚拟关闭出错!", getApplicationInfo().getApplicationName(), throwable);
            return false;
        }).join();
    }

    @Override
    public boolean physicalClose() {
        return asyncPhysicalClose().join();
    }

    private CompletableFuture<Boolean> asyncPhysicalClose() {
        return CompletableFuture.supplyAsync(() -> {
            logger.info("{} 应用物理关闭...", getApplicationInfo().getApplicationName());
            for (InstanceManager instanceManager : instanceManagerList) {
                boolean instanceClose = instanceManager.physicalClose();
                if (instanceClose) {
                    // 关闭成功
                    handleCloseSuccessful(instanceManager.getInstanceInfo());
                } else {
                    // 关闭失败
                    logger.info("{} 应用物理关闭失败", getApplicationInfo().getApplicationName());
                    return false;
                }
            }
            physicalClosed = true;
            logger.info("{} 应用物理关闭成功", getApplicationInfo().getApplicationName());
            return physicalClosed;
        }, taskExecutor).exceptionally(throwable -> {
            logger.warn("{} 应用物理关闭出错!", getApplicationInfo().getApplicationName(), throwable);
            return false;
        });
    }

    // 关闭成功的处理
    private void handleCloseSuccessful(InstanceInfo instanceInfo) {
        synchronized (startedInstances) {
            if (startedInstances.contains(instanceInfo.getInstanceId())) {
                startedInstances.remove(instanceInfo.getInstanceId());
                notStartedInstances.add(instanceInfo.getInstanceId());
            }
        }
    }

    // 启动成功的处理
    private void handleLaunchSuccessful(InstanceInfo instanceInfo) {
        synchronized (startedInstances) {
            if (notStartedInstances.contains(instanceInfo.getInstanceId())) {
                notStartedInstances.remove(instanceInfo.getInstanceId());
                startedInstances.add(instanceInfo.getInstanceId());
            }
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof RefreshEvent) {
            RefreshEvent refreshEvent = (RefreshEvent) event;
            if (refreshEvent.getApplicationId() != null &&
                    !getApplicationInfo().getApplicationId().equals(refreshEvent.getApplicationId())) {
                return;
            }
            if (refreshEvent.getApplicationId() != null &&
                    refreshEvent.getInstanceId() != null) {
                return;
            }
            logger.info("{} 应用的配置更新！", getApplicationInfo().getApplicationName());
            GuardContext guardContext = refreshEvent.getSource();
            ApplicationInfo applicationInfo = guardContext.getApplicationInfoSupplier()
                    .apply(getApplicationConfig());
            applicationPairReference.set(Pair.newPair(getApplicationConfig(), applicationInfo));
        } else if (event instanceof CloseInstanceEvent) {
            // 物理关闭
            CloseInstanceEvent closeInstanceEvent = (CloseInstanceEvent) event;
            if (getApplicationInfo().getApplicationName().equals(closeInstanceEvent.getApplicationName())) {
                logger.info("实例{}关闭通知！", closeInstanceEvent.getInstanceInfo().getInstanceId());
                // 移除关闭的实例
                handleCloseSuccessful(closeInstanceEvent.getInstanceInfo());
            }
        }
    }

    // 维护实例列表
    private void defendInstances() {
        logger.info("{} 应用维护实例列表。。。", getApplicationInfo().getApplicationName());
        if (virtualClosed || physicalClosed) {
            logger.info("{} 应用维护实例列表成功，虚拟关闭:{}, 物理关闭:{}",
                    getApplicationInfo().getApplicationName(), virtualClosed, physicalClosed);
            return;
        }
        defendStartedInstances();
        defendNotStartedInstances();
        logger.info("{} 应用维护实例列表成功", getApplicationInfo().getApplicationName());
    }

    // 维护启动的实例列表
    private void defendStartedInstances() {
        // 移除不允许启动的实例、已经关闭的实例
        Iterator<String> iterator = startedInstances.iterator();
        while (iterator.hasNext()) {
            String instanceId = iterator.next();
            InstanceManager instanceManager = instanceManagerMap.get(instanceId);
            InstanceInfo instanceInfo = instanceManager.getInstanceInfo();
            // 移除不允许启动的实例
            if (instanceInfo.getStatus().equals(LaunchStatus.SHUTDOWN)) {
                logger.info("应用：{}的实例{}不允许启动，移动到未启动实例列表",
                        getApplicationInfo().getApplicationName(),
                        instanceId);
                iterator.remove();
                notStartedInstances.add(instanceId);
                continue;
            }
            // 移除已经关闭的实例
            if (instanceManager.isPhysicalClosed() || instanceManager.isVirtualClosed()) {
                logger.info("应用：{}的实例{}已经关闭，移动到未启动实例列表",
                        getApplicationInfo().getApplicationName(),
                        instanceId);
                iterator.remove();
                notStartedInstances.add(instanceId);
                continue;
            }
        }
        int num = startedInstances.size() - getApplicationInfo().getStartInstanceNum();
        if (num > 0) {
            // 移除多余启动的实例
            List<String> list = instanceSortByWeight(startedInstances);
            for (int i = 0; i < list.size() && num > 0; i++) {
                String instanceId = list.get(i);
                InstanceManager instanceManager = instanceManagerMap.get(instanceId);
                if (instanceManager.physicalClose()) {
                    logger.info("应用：{}移除多余启动的实例{}，移动到未启动实例列表",
                            getApplicationInfo().getApplicationName(),
                            instanceId);
                    num--;
                    handleCloseSuccessful(instanceManager.getInstanceInfo());
                }
            }
        }
    }

    // 维护未启动的实例列表
    private void defendNotStartedInstances() {
        int num = startedInstances.size() - getApplicationInfo().getStartInstanceNum();
        num = num > 0 ? 0 : -num;
        List<String> list = instanceSortByWeight(notStartedInstances);
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String instanceId = iterator.next();
            InstanceManager instanceManager = instanceManagerMap.get(instanceId);
            // 关闭不允许启动的实例
            InstanceInfo instanceInfo = instanceManager.getInstanceInfo();
            if (instanceInfo.getStatus().equals(LaunchStatus.SHUTDOWN)) {
                logger.info("应用：{}的实例{}不允许启动，关闭实例",
                        getApplicationInfo().getApplicationName(),
                        instanceId);
                instanceManager.physicalClose();
                continue;
            }
            if (num > 0) {
                // 启动实例
                logger.info("应用：{} 启动实例{}",
                        getApplicationInfo().getApplicationName(),
                        instanceId);
                if (instanceManager.launch()) {
                    logger.info("应用：{} 启动实例{}成功",
                            getApplicationInfo().getApplicationName(),
                            instanceId);
                    handleLaunchSuccessful(instanceInfo);
                    num--;
                } else {
                    logger.info("应用：{} 启动实例{}失败",
                            getApplicationInfo().getApplicationName(),
                            instanceId);
                }
            } else {
                // 关闭实例
                logger.info("应用：{} 关闭实例{}",
                        getApplicationInfo().getApplicationName(),
                        instanceId);
                instanceManager.physicalClose();
            }
        }
    }

    // 维护实例列表任务
    private class DefendInstancesTask implements Runnable {
        @Override
        public void run() {
            CompletableFuture.runAsync(() -> {
                long startTime = DateTimeUtils.getCurrentTime();
                try {
                    defendInstances();
                } catch (Exception e) {
                    logger.warn("{} 维护实例列表出错!", getApplicationInfo().getApplicationName(), e);
                } finally {
                    if (virtualClosed || physicalClosed) {
                        return;
                    }
                    long delay = DateTimeUtils.delayTime(startTime +
                            getApplicationInfo().getDefendInstanceDuration());
                    scheduled.schedule(this, delay, TimeUnit.MILLISECONDS);
                }
            }, taskExecutor);
        }
    }

    private List<String> instanceSortByWeight(List<String> instanceIds) {
        List<Pair<String, Integer>> collect = instanceIds.stream()
                .map(instanceId -> {
                    InstanceManager instanceManager = instanceManagerMap.get(instanceId);
                    return Pair.newPair(instanceId, instanceManager.getInstanceInfo().getWeight());
                }).collect(Collectors.toList());
        Pair<String, Integer>[] pairs = collect.toArray(new Pair[collect.size()]);
        Arrays.sort(pairs, new InstanceComparator());
        return Arrays.stream(pairs)
                .map(pair -> pair.getFirst())
                .collect(Collectors.toList());
    }

    /**
     * 实例启动的排序：根据权重排序
     */
    private class InstanceComparator implements Comparator<Pair<String, Integer>> {
        @Override
        public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
            return o2.getSecond().compareTo(o1.getSecond());
        }
    }

    private ApplicationConfig getApplicationConfig() {
        return applicationPairReference.get().getFirst();
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationPairReference.get().getSecond();
    }


    // 动态添加实例
    @Override
    public boolean addInstance(InstanceConfig instanceConfig) {
        CompletableFuture.runAsync(() -> {
            InstanceManager instanceManager = new InstanceManager(context,
                    this, instanceConfig);
            instanceManagerMap.put(instanceManager.getInstanceInfo().getInstanceId(),
                    instanceManager);
            instanceManagerList.add(instanceManager);
            notStartedInstances.add(instanceManager.getInstanceInfo().getInstanceId());
            logger.debug("动态添加实例{}成功", instanceManager.getInstanceInfo().getInstanceId());
        }, taskExecutor).join();
        return true;
    }

    // 动态移除实例
    @Override
    public boolean removeInstance(Integer applicationId, String instanceId) {
        if (!applicationId.equals(getApplicationInfo().getApplicationId())) {
            logger.debug("不属于该应用! 该应用：{}，实际应用：{}", getApplicationInfo().getApplicationId(),
                    applicationId);
            return true;
        }
        return CompletableFuture.supplyAsync(() -> {
            InstanceManager instanceManager = instanceManagerMap.get(instanceId);
            if (instanceManager == null) {
                logger.debug("移除应用{}中的实例{}，实例不存在", getApplicationInfo().getApplicationName(),
                        instanceId);
                return true;
            }
            if (instanceManager.physicalClose()) {
                instanceManagerMap.remove(instanceId);
                instanceManagerList.remove(instanceManager);
                startedInstances.remove(instanceId);
                notStartedInstances.remove(instanceId);
                instanceManager.selfClose();
                logger.debug("{} 动态移除实例{}成功", getApplicationInfo().getApplicationName(),
                        instanceId);
                return true;
            }
            logger.debug("{} 动态移除实例{}失败", getApplicationInfo().getApplicationName(),
                    instanceId);
            return false;
        }, taskExecutor).join();
    }

    @Override
    public InstanceInfo[] queryInstance(InstanceCondition condition) {
        // TODO: 2018/5/11
        return new InstanceInfo[0];
    }

    @Override
    public InstanceInfo getInstance(Integer applicationId, String instanceId) {
        if (!applicationId.equals(getApplicationInfo().getApplicationId())) {
            logger.debug("不属于该应用! 该应用：{}，实际应用：{}", getApplicationInfo().getApplicationId(),
                    applicationId);
            return null;
        }
        Optional<InstanceInfo> optional = instanceManagerList.stream()
                .map(instanceManager -> instanceManager.getInstanceInfo())
                .filter(instanceInfo -> instanceInfo.getInstanceId().equals(instanceId))
                .findFirst();
        return optional.orElse(null);
    }

    public boolean isVirtualClosed() {
        return virtualClosed;
    }

    public boolean isPhysicalClosed() {
        return physicalClosed;
    }
}


