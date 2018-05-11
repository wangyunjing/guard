package com.wyj.guard.bootstrap;


import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.ApplicationManager;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.web.ApplicationCondition;
import com.wyj.guard.web.InstanceCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 单机启动类
 */
public class SingleLauncher extends AbstractLauncher {

    private Logger logger = LoggerFactory.getLogger(SingleLauncher.class);

    private List<ApplicationManager> applicationManagers;

    public SingleLauncher(GuardContext guardContext) {
        super(guardContext);
    }

    @Override
    public boolean launch() {
        logger.debug("开始启动单机版...");
        // 生成应用管理器
        applicationManagers = new ArrayList<>();
        for (ApplicationConfig applicationConfig : guardContext.getAppConfigLoader().load()) {
            ApplicationManager applicationManager = new ApplicationManager(
                    (ConfigurableGuardContext) guardContext, applicationConfig);
            applicationManagers.add(applicationManager);
        }

        for (int i = 0; i < applicationManagers.size(); i++) {
            if (!applicationManagers.get(i).launch()) {
                for (int j = 0; j < i; j++) {
                    applicationManagers.get(j).physicalClose();
                }
                return false;
            }
        }
        logger.debug("启动单机版成功!");
        launched = true;
        return true;
    }

    @Override
    public void close() {
        logger.info("关闭所有实例...");
        applicationManagers.forEach(applicationManager -> {
            if (!applicationManager.isPhysicalClosed()) {
                applicationManager.physicalClose();
            }
        });
    }

    @Override
    public void open() {
        logger.info("开启所有实例...");
        applicationManagers.forEach(applicationManager -> {
            if (applicationManager.isVirtualClosed() ||
                    applicationManager.isPhysicalClosed()) {
                applicationManager.launch();
            }
        });
    }

    @Override
    public boolean removeApplication(Integer applicationId) {
        logger.debug("开始移除应用{}", applicationId);
        Optional<ApplicationManager> optional = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(applicationId))
                .findFirst();
        if (optional.isPresent()) {
            ApplicationManager applicationManager = optional.get();
            if (applicationManager.physicalClose()) {
                applicationManagers.remove(applicationManager);
                applicationManager.selfClose();
                logger.debug("移除应用{}成功!", applicationId);
                return true;
            }
            logger.debug("移除应用{}失败!", applicationId);
            return false;
        }
        logger.debug("移除应用{}，应用不存在!", applicationId);
        return false;
    }

    @Override
    public boolean removeInstance(Integer applicationId, String instanceId) {
        logger.debug("开始移除实例，应用ID：{}，实例ID：{}", applicationId, instanceId);
        Optional<ApplicationManager> optional = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(applicationId))
                .findFirst();
        if (optional.isPresent()) {
            ApplicationManager applicationManager = optional.get();
            if (applicationManager.removeInstance(applicationId, instanceId)) {
                logger.debug("移除实例成功，应用ID：{}，实例ID：{}", applicationId, instanceId);
                return true;
            }
            logger.debug("移除实例失败，应用ID：{}，实例ID：{}", applicationId, instanceId);
            return false;
        }
        logger.debug("移除实例失败，应用不存在，应用ID：{}，实例ID：{}", applicationId, instanceId);
        return false;
    }

    @Override
    public boolean addApplication(ApplicationConfig applicationConfig) {
        logger.debug("添加应用...");
        ApplicationManager applicationManager = new ApplicationManager(
                (ConfigurableGuardContext) guardContext, applicationConfig);
        applicationManagers.add(applicationManager);
        applicationManager.launch();
        logger.debug("添加应用 {} 成功!", applicationManager.getApplicationInfo().getApplicationId());
        return true;
    }

    @Override
    public boolean addInstance(InstanceConfig instanceConfig) {
        logger.debug("添加实例...");
        Integer applicationId = instanceConfig.getApplicationId();
        Optional<ApplicationManager> optional = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(applicationId))
                .findFirst();
        if (optional.isPresent()) {
            ApplicationManager applicationManager = optional.get();
            applicationManager.addInstance(instanceConfig);
            if (logger.isDebugEnabled()) {
                logger.debug("添加实例成功! 应用ID: {}, 实例ID: {}", applicationId,
                        instanceConfig.getInstanceId());
            }
            return true;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("添加实例失败，应用不存在! 应用ID：{}, 实例ID：{}", applicationId,
                    instanceConfig.getInstanceId());
        }
        return false;
    }

    @Override
    public ApplicationInfo[] queryApplication(ApplicationCondition condition) {
        // TODO: 2018/5/11
        return new ApplicationInfo[0];
    }

    @Override
    public InstanceInfo[] queryInstance(InstanceCondition condition) {
        // TODO: 2018/5/11
        return new InstanceInfo[0];
    }

    @Override
    public ApplicationInfo getApplication(Integer applicationId) {
        Optional<ApplicationManager> optional = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(applicationId))
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getApplicationInfo();
        }
        return null;
    }

    @Override
    public InstanceInfo getInstance(Integer applicationId, String instanceId) {
        Optional<ApplicationManager> optional = applicationManagers.stream()
                .filter(applicationManager -> applicationManager.getApplicationInfo()
                        .getApplicationId().equals(applicationId))
                .findFirst();
        if (optional.isPresent()) {
            ApplicationManager applicationManager = optional.get();
            return applicationManager.getInstance(applicationId, instanceId);
        }
        return null;
    }
}
