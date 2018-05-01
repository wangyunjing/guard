package com.wyj.guard.info;

import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.share.enums.LaunchStatus;

/**
 * 应用信息
 */
public class ApplicationInfo implements ApplicationConfig{

    private static final Integer DEFAULT_MIN_TIME = 3000;

    // 监控服务器可用的周期
    private static final Integer DEFAULT_MONITOR_SERVER_AVAILABLE_DURATION = 20000;

    // 监控实例个数的周期
    private static final Integer DEFAULT_MONITORING_INSTANCE_NUM_DURATION = 20000;

    // 应用Id
    private Integer applicationId;

    // 应用名称
    private String applicationName;

    // 默认启动端口号
    private Integer port;

    // 默认健康检查URL
    private String healthUrl;

    // 默认启动命令
    private String startCommand;

    // 启动实例的个数
    private Integer startInstanceNum;

    // 默认远程服务器的用户名
    private String username;

    // 默认远程服务器的密码
    private String password;

    // 监控服务器可用的周期
    private Integer monitorServerAvailableDuration;

    // 监控实例个数的周期
    private Integer monitoringInstanceNumDuration;

    // 默认心跳检查频率(毫秒)
    private Integer heartbeatRate;

    // 默认初始化实例时长(毫秒)
    private Integer initializeInstanceDuration;

    // 默认自我保护周期(毫秒)
    private Integer selfProtectedDuration;

    // 状态
    private LaunchStatus status;

    public static class Builder {
        private ApplicationInfo applicationInfo;

        private Builder(ApplicationInfo applicationInfo) {
            this.applicationInfo = applicationInfo;
        }

        public static Builder newBuilder() {
            return new Builder(new ApplicationInfo());
        }

        public static Builder newBuilder(ApplicationInfo applicationInfo) {
            return new Builder(applicationInfo);
        }

        public ApplicationInfo build() {
            if (isSetDefaultValue(applicationInfo.getMonitorServerAvailableDuration())) {
                this.applicationInfo.monitorServerAvailableDuration = ApplicationInfo.DEFAULT_MONITOR_SERVER_AVAILABLE_DURATION;
            }
            if (isSetDefaultValue(applicationInfo.getMonitoringInstanceNumDuration())) {
                this.applicationInfo.monitoringInstanceNumDuration = ApplicationInfo.DEFAULT_MONITORING_INSTANCE_NUM_DURATION;
            }
            return this.applicationInfo;
        }

        public Builder setApplicationId(Integer applicationId) {
            this.applicationInfo.applicationId = applicationId;
            return this;
        }

        public Builder setApplicationName(String applicationName) {
            this.applicationInfo.applicationName = applicationName;
            return this;
        }

        public Builder setDefaultPort(Integer port) {
            this.applicationInfo.port = port;
            return this;
        }

        public Builder setDefaultHealthUrl(String healthUrl) {
            this.applicationInfo.healthUrl = healthUrl;
            return this;
        }

        public Builder setDefaultStartCommand(String startCommand) {
            this.applicationInfo.startCommand = startCommand;
            return this;
        }

        public Builder setStartInstanceNum(Integer startInstanceNum) {
            this.applicationInfo.startInstanceNum = startInstanceNum;
            return this;
        }

        public Builder setDefaultUsername(String username) {
            this.applicationInfo.username = username;
            return this;
        }

        public Builder setDefaultPassword(String password) {
            this.applicationInfo.password = password;
            return this;
        }

        public Builder setMonitorServerAvailableDuration(Integer monitorServerAvailableDuration) {
            this.applicationInfo.monitorServerAvailableDuration = monitorServerAvailableDuration;
            return this;
        }

        public Builder setMonitoringInstanceNumDuration(Integer monitoringInstanceNumDuration) {
            this.applicationInfo.monitoringInstanceNumDuration = monitoringInstanceNumDuration;
            return this;
        }

        public Builder setDefaultHeartbeatRate(Integer heartbeatRate) {
            this.applicationInfo.heartbeatRate = heartbeatRate;
            return this;
        }

        public Builder setDefaultInitializeInstanceDuration(Integer initializeInstanceDuration) {
            this.applicationInfo.initializeInstanceDuration = initializeInstanceDuration;
            return this;
        }

        public Builder setDefaultSelfProtectedDuration(Integer selfProtectedDuration) {
            this.applicationInfo.selfProtectedDuration = selfProtectedDuration;
            return this;
        }

        public Builder setStatus(LaunchStatus status) {
            this.applicationInfo.status = status;
            return this;
        }

        private boolean isSetDefaultValue(Integer currentValue) {
            if (currentValue == null || currentValue <= DEFAULT_MIN_TIME) {
                return true;
            }
            return false;
        }
    }

    @Override
    public Integer getApplicationId() {
        return applicationId;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public Integer getDefaultPort() {
        return port;
    }

    @Override
    public String getDefaultHealthUrl() {
        return healthUrl;
    }

    @Override
    public String getDefaultStartCommand() {
        return startCommand;
    }

    @Override
    public Integer getStartInstanceNum() {
        return startInstanceNum;
    }

    @Override
    public String getDefaultUsername() {
        return username;
    }

    @Override
    public String getDefaultPassword() {
        return password;
    }

    @Override
    public Integer getMonitorServerAvailableDuration() {
        return monitorServerAvailableDuration;
    }

    @Override
    public Integer getMonitoringInstanceNumDuration() {
        return monitoringInstanceNumDuration;
    }

    @Override
    public Integer getDefaultHeartbeatRate() {
        return heartbeatRate;
    }

    @Override
    public Integer getDefaultInitializeInstanceDuration() {
        return initializeInstanceDuration;
    }

    @Override
    public Integer getDefaultSelfProtectedDuration() {
        return selfProtectedDuration;
    }

    @Override
    public LaunchStatus getStatus() {
        return status;
    }
}
