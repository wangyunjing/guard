package com.wyj.guard.info;

import com.alibaba.druid.util.DaemonThreadFactory;
import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.event.ApplicationEvent;
import com.wyj.guard.context.event.ApplicationListener;
import com.wyj.guard.context.event.CloseInstanceEvent;
import com.wyj.guard.context.event.RefreshEvent;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.remote.SSHClient;
import com.wyj.guard.share.Closeable;
import com.wyj.guard.share.Pair;
import com.wyj.guard.share.enums.InstanceStatus;
import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.utils.DateTimeUtils;
import com.wyj.guard.utils.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 实例管理器 {@link InstanceInfo}
 */
public class InstanceManager implements Closeable, ApplicationListener<ApplicationEvent> {

    private Logger logger = LoggerFactory.getLogger(InstanceManager.class);

    private ConfigurableGuardContext context;

    private ApplicationManager applicationManager;

    // 实例配置和实例实体的聚合
    private AtomicReference<Pair<InstanceConfig, InstanceInfo>> instancePairReference;

    // 实例启动器
    private SSHClient sshClient;

    // 心跳检查调度器（执行心跳任务）
    private ScheduledExecutorService heartbeatScheduled;

    // 执行任务线程池 (执行状态改变通知的任务)
    private ExecutorService taskExecutor;

    // 启动时间
    private Long launchTime;

    // 虚拟关闭时间
    private Long virtualClosedTime;

    // 物理关闭时间
    private Long physicalClosedTime;

    // 上次宕机的时间 (DOWN, SERVER_DOWN, SHUTDOWN)
    private AtomicLong lastDownTime;

    // 上次执行心跳检测的时间
    private AtomicLong lastHeartbeatTime;

    // 虚拟关闭
    private volatile boolean virtualClosed = true;

    // 物理关闭
    private volatile boolean physicalClosed = true;

    public InstanceManager(ConfigurableGuardContext context,
                           ApplicationManager applicationManager,
                           InstanceConfig instanceConfig) {
        this.context = context;
        this.applicationManager = applicationManager;
        this.instancePairReference = new AtomicReference<>(Pair.newPair(instanceConfig,
                context.getInstanceInfoSupplier().apply(instanceConfig)));
        this.sshClient = context.getSSHClient();
        // 添加应用监听器
        context.addApplicationListener(this);
    }

    public synchronized boolean launch() {
        // 初始化心跳检查调度器
        if (heartbeatScheduled == null || heartbeatScheduled.isShutdown()) {
            heartbeatScheduled = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory(
                    getInstanceInfo().getApplicationName() + "-" + getInstanceInfo().getInstanceId() +
                            "-Instance-Heartbeat-Scheduled"));
        }
        // 初始化任务线程池
        if (taskExecutor == null || taskExecutor.isShutdown()) {
            taskExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory(
                    getInstanceInfo().getApplicationName() + "-" + getInstanceInfo().getInstanceId() +
                            "-Instance-Exec-Task-ThreadPool"));
        }
        boolean launched = false;
        // 是否可以启动
        if (getInstanceInfo().getStatus().equals(LaunchStatus.SHUTDOWN)) {
            logger.warn("实例{}不可启动，启动状态：{}", getInstanceInfo().getInstanceId(),
                    getInstanceInfo().getStatus());
            return false;
        }
        InstanceStatus instanceStatus = getInstanceStatus();
        // 检测是否启动
        if (instanceStatus == InstanceStatus.UP ||
                instanceStatus == InstanceStatus.DOWN) {
            logger.info("实例{}已经启动! 当前状态为{}", getInstanceInfo().getInstanceId(),
                    instanceStatus);
            launched = true;
        } else {
            // 启动实例
            launched = sshClient.startInstance(getInstanceInfo());
        }
        if (launched) {
            // 设置启动时间
            launchTime = DateTimeUtils.getCurrentTime();
            virtualClosedTime = null;
            physicalClosedTime = null;
            virtualClosed = false;
            physicalClosed = false;
            // 第一次心跳检测，延期为实例初始化时长
            heartbeatScheduled.schedule(new HeartbeatTask(),
                    getInstanceInfo().getInitializeInstanceDuration(),
                    TimeUnit.MILLISECONDS);
        }
        return launched;
    }

    @Override
    public boolean selfClose() {
        ThreadPoolUtils.shutdown(heartbeatScheduled);
        ThreadPoolUtils.shutdown(taskExecutor);
        heartbeatScheduled = null;
        taskExecutor = null;
        context.removeApplicationListener(this);
        return true;
    }

    @Override
    public synchronized boolean virtualClose() {
        if (virtualClosed) {
            return virtualClosed;
        }
        logger.info("虚拟关闭实例{}...", getInstanceInfo().getInstanceId());
        virtualClosed = true;
        virtualClosedTime = DateTimeUtils.getCurrentTime();
        logger.info("虚拟关闭实例{}成功", getInstanceInfo().getInstanceId());
        return virtualClosed;
    }

    @Override
    public synchronized boolean physicalClose() {
        logger.info("物理关闭实例{}...", getInstanceInfo().getInstanceId());
        boolean result = sshClient.closeInstance(getInstanceInfo());
        if (result) {
            physicalClosed = true;
            physicalClosedTime = DateTimeUtils.getCurrentTime();
            logger.info("物理关闭实例{}成功", getInstanceInfo().getInstanceId());
        }
        return result;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof RefreshEvent) {
            RefreshEvent refreshEvent = (RefreshEvent) event;
            if (refreshEvent.getApplicationId() != null &&
                    !applicationManager.getApplicationInfo().getApplicationId().equals(refreshEvent.getApplicationId())) {
                return;
            }
            if (refreshEvent.getApplicationId() != null &&
                    refreshEvent.getInstanceId() != null &&
                    !getInstanceInfo().getInstanceId().equals(refreshEvent.getInstanceId())) {
                return;
            }
            logger.info("实例{}的配置更新！", getInstanceInfo().getInstanceId());
            GuardContext context = refreshEvent.getSource();
            InstanceInfo instanceInfo = context.getInstanceInfoSupplier().apply(getInstanceConfig());
            instancePairReference.set(Pair.newPair(getInstanceConfig(), instanceInfo));
        }
    }

    // 心跳检测
    private synchronized void heartbeat() {
        if (virtualClosed || physicalClosed) {
            return;
        }
        long time = DateTimeUtils.getCurrentTime();
        if (lastHeartbeatTime == null) {
            lastHeartbeatTime = new AtomicLong();
        }
        lastHeartbeatTime.set(time);
        logger.info("实例{}心跳检测...", getInstanceInfo().getInstanceId());
        InstanceStatus status = getInstanceStatus();
        logger.info("实例{}当前的状态为{}", getInstanceInfo().getInstanceId(), status);
        switch (status) {
            case UP:
                handleUp();
                break;
            case DOWN:
                handleDown(status);
                break;
            case SHUTDOWN:
                handleDown(status);
                break;
            case SERVER_DOWN:
                handleDown(status);
                break;
            default:
                handleDown(status);
        }
    }

    // 处理UP
    private void handleUp() {
        lastDownTime = null;
    }

    // 处理DOWN和SERVER_DOWN以及SHUTDOWN
    private void handleDown(InstanceStatus status) {
        long time = DateTimeUtils.getCurrentTime();
        if (lastDownTime == null) {
            lastDownTime = new AtomicLong(time);
        }
        if (lastDownTime.get() + getInstanceInfo().getSelfProtectedDuration() < time) {
            logger.warn("实例{}自我保护模式已经超过，物理关闭实例，当前状态：{}",
                    getInstanceInfo().getInstanceId(), status);
            if (physicalClose()) {
                CloseInstanceEvent closeInstanceEvent = new CloseInstanceEvent(
                        applicationManager.getApplicationInfo().getApplicationName(),
                        getInstanceInfo(), status);
                // 发布实例关闭事件
                context.publishEvent(closeInstanceEvent);
            }
        } else {
            logger.warn("实例{}处于自我保护模式，当前状态：{}",
                    getInstanceInfo().getInstanceId(), status);
        }
    }

    // 心跳任务
    private class HeartbeatTask implements Runnable {
        @Override
        public void run() {
            CompletableFuture.runAsync(() -> {
                try {
                    heartbeat();
                } catch (Throwable e) {
                    logger.warn("实例{}执行心跳出错！", getInstanceInfo().getInstanceId(), e);
                } finally {
                    if (virtualClosed || physicalClosed) {
                        return;
                    }
                    long delay = DateTimeUtils.delayTime(lastHeartbeatTime.get() +
                            getInstanceInfo().getHeartbeatRate());
                    heartbeatScheduled.schedule(this, delay, TimeUnit.MILLISECONDS);
                }
            }, taskExecutor);
        }

    }

    private InstanceConfig getInstanceConfig() {
        return instancePairReference.get().getFirst();
    }

    public InstanceInfo getInstanceInfo() {
        return instancePairReference.get().getSecond();
    }

    public InstanceStatus getInstanceStatus() {
        return sshClient.getInstanceStatus(getInstanceInfo());
    }

    public Long getLaunchTime() {
        return launchTime;
    }

    public Long getVirtualClosedTime() {
        return virtualClosedTime;
    }

    public Long getPhysicalClosedTime() {
        return physicalClosedTime;
    }

    public AtomicLong getLastDownTime() {
        return lastDownTime;
    }

    public AtomicLong getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public boolean isVirtualClosed() {
        return virtualClosed;
    }

    public boolean isPhysicalClosed() {
        return physicalClosed;
    }


}


