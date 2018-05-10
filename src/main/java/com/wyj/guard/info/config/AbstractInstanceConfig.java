package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方便扩展
 */
public abstract class AbstractInstanceConfig implements ConfigurableInstanceConfig {

    public final Logger logger = LoggerFactory.getLogger(AbstractInstanceConfig.class);

    // 当前实例所属应用的配置
    protected ApplicationConfig applicationConfig;

    // 当前实例ID
    protected String instanceId;

    public AbstractInstanceConfig(ApplicationConfig applicationConfig, String instanceId) {
        this.applicationConfig = applicationConfig;
        this.instanceId = instanceId;
    }

    @Override
    public final Integer getApplicationId() {
        return applicationConfig.getApplicationId();
    }

    @Override
    public final String getApplicationName() {
        return applicationConfig.getApplicationName();
    }


    @Override
    public final String getInstanceId() {
        return this.instanceId;
    }

    @Override
    public final Integer getPort() {
        Integer port = doGetPort();

        port = port != null ? port : applicationConfig.getDefaultPort();

        if (port == null) {
            // TODO: 2018/1/25
            throw new RuntimeException("没有设置端口号");
        }
        return port;
    }

    protected abstract Integer doGetPort();

    @Override
    public final String getStartCommand() {
        String startCommand = doGetStartCommand();

        startCommand = startCommand != null ? startCommand : applicationConfig.getDefaultStartCommand();

        if (startCommand == null) {
            // TODO: 2018/1/25
            throw new RuntimeException("没有设置启动命令");
        }
        return startCommand;
    }

    protected abstract String doGetStartCommand();

    @Override
    public final String getHealthUrl() {
        String healthUrl = doGetHealthUrl();

        healthUrl = healthUrl != null ? healthUrl : applicationConfig.getDefaultHealthUrl();

        if (healthUrl == null) {
            // TODO: 2018/1/25
            throw new RuntimeException("没有设置检查URL");
        }
        return healthUrl;
    }

    protected abstract String doGetHealthUrl();

    @Override
    public final Integer getWeight() {
        Integer weight = doGetWeight();

        weight = weight != null ? weight : 0;

        return weight;
    }

    protected abstract Integer doGetWeight();

    @Override
    public final String getUsername() {
        String username = doGetUsername();

        username = username != null ? username : applicationConfig.getDefaultUsername();

        if (username == null) {
            // TODO: 2018/1/25
            throw new RuntimeException("没有设置远程服务器的用户名");
        }
        return username;
    }

    protected abstract String doGetUsername();

    @Override
    public final String getPassword() {
        String password = doGetPassword();

        password = password != null ? password : applicationConfig.getDefaultPassword();

        if (password == null) {
            // TODO: 2018/1/25
            throw new RuntimeException("没有设置远程服务器的密码");
        }
        return password;
    }

    protected abstract String doGetPassword();

    @Override
    public final Integer getHeartbeatRate() {
        Integer heartbeatRate = doGetHeartbeatRate();

        heartbeatRate = heartbeatRate != null ? heartbeatRate : applicationConfig.getDefaultHeartbeatRate();

        return heartbeatRate;
    }

    protected abstract Integer doGetHeartbeatRate();

    @Override
    public final Integer getInitializeInstanceDuration() {
        Integer initializeInstanceDuration = doGetInitializeInstanceDuration();

        initializeInstanceDuration = initializeInstanceDuration != null ? initializeInstanceDuration : applicationConfig.getDefaultInitializeInstanceDuration();

        return initializeInstanceDuration;
    }

    protected abstract Integer doGetInitializeInstanceDuration();

    @Override
    public final Integer getSelfProtectedDuration() {
        Integer selfProtectedDuration = doGetSelfProtectedDuration();

        selfProtectedDuration = selfProtectedDuration != null ? selfProtectedDuration : applicationConfig.getDefaultSelfProtectedDuration();

        return selfProtectedDuration;
    }

    protected abstract Integer doGetSelfProtectedDuration();

    @Override
    public final LaunchStatus getStatus() {
        LaunchStatus applicationConfigStatus = applicationConfig.getStatus();
        if (applicationConfigStatus.equals(LaunchStatus.SHUTDOWN)) {
            return LaunchStatus.SHUTDOWN;
        }
        return doGetStatus();
    }

    protected abstract LaunchStatus doGetStatus();
}
