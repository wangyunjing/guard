package com.wyj.guard.info;


import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.share.enums.LaunchStatus;

public class InstanceInfo implements InstanceConfig {

    private static final Integer DEFAULT_MIN_TIME = 3000;

    // 默认心跳检查频率(毫秒)
    private static final Integer DEFAULT_HEARTBEAT_RATE = 15000;

    // 默认初始化实例时长(毫秒)
    private static final Integer DEFAULT_INITIALIZE_INSTANCE_DURATION = 60000;

    // 默认自我保护周期(毫秒)
    private static final Integer DEFAULT_SELF_PROTECTED_DURATION = 60000;

    // 所属应用名
    private String applicationName;

    // 实例名
    private String instanceId;

    // ip地址
    private String ip;

    // 应用启动端口号
    private Integer port;

    // 应用健康检查URL
    private String healthUrl;

    // 权重（用于启动实例）
    private Integer weight;

    // 启动命令
    private String startCommand;

    // 远程服务器用户名
    private String username;

    // 远程服务器密码
    private String password;

    // 心跳频率（毫秒）
    private Integer heartbeatRate;

    // 初始化实例时长
    private Integer initializeInstanceDuration;

    // 自我保护的时长（出现第一次down时开始计算）
    private Integer selfProtectedDuration;

    private LaunchStatus status;

    public static class Builder{

        private InstanceInfo instanceInfo;

        private Builder(InstanceInfo instanceInfo) {
            this.instanceInfo = instanceInfo;
        }

        public static Builder newBuilder() {
            return new Builder(new InstanceInfo());
        }

        public static Builder newBuilder(InstanceInfo instanceInfo) {
            return new Builder(instanceInfo);
        }

        public InstanceInfo build() {
            if (isSetDefaultValue(instanceInfo.getHeartbeatRate())) {
                instanceInfo.heartbeatRate = InstanceInfo.DEFAULT_HEARTBEAT_RATE;
            }
            if (isSetDefaultValue(instanceInfo.getInitializeInstanceDuration())) {
                instanceInfo.initializeInstanceDuration = InstanceInfo.DEFAULT_INITIALIZE_INSTANCE_DURATION;
            }
            if (isSetDefaultValue(instanceInfo.getSelfProtectedDuration())) {
                instanceInfo.selfProtectedDuration = InstanceInfo.DEFAULT_SELF_PROTECTED_DURATION;
            }
            return this.instanceInfo;
        }

        public Builder setApplicationName(String applicationName) {
            this.instanceInfo.applicationName = applicationName;
            return this;
        }

        public Builder setInstanceId(String instanceId) {
            this.instanceInfo.instanceId = instanceId;
            return this;
        }

        public Builder setIp(String ip) {
            this.instanceInfo.ip = ip;
            return this;
        }

        public Builder setPort(Integer port) {
            this.instanceInfo.port = port;
            return this;
        }

        public Builder setHealthUrl(String healthUrl) {
            this.instanceInfo.healthUrl = healthUrl;
            return this;
        }

        public Builder setWeight(Integer weight) {
            this.instanceInfo.weight = weight;
            return this;
        }

        public Builder setStartCommand(String startCommand) {
            this.instanceInfo.startCommand = startCommand;
            return this;
        }

        public Builder setUsername(String username) {
            this.instanceInfo.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.instanceInfo.password = password;
            return this;
        }

        public Builder setHeartbeatRate(Integer heartbeatRate) {
            this.instanceInfo.heartbeatRate = heartbeatRate;
            return this;
        }
        public Builder setInitializeInstanceDuration(Integer initializeInstanceDuration) {
            this.instanceInfo.initializeInstanceDuration = initializeInstanceDuration;
            return this;
        }
        public Builder setSelfProtectedDuration(Integer selfProtectedDuration) {
            this.instanceInfo.selfProtectedDuration = selfProtectedDuration;
            return this;
        }

        public Builder setStatus(LaunchStatus status) {
            this.instanceInfo.status = status;
            return this;
        }

        private boolean isSetDefaultValue(Integer currentValue) {
            if (currentValue == null || currentValue <= DEFAULT_MIN_TIME) {
                return true;
            }
            return false;
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getHealthUrl() {
        return healthUrl;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getHeartbeatRate() {
        return heartbeatRate;
    }

    public Integer getInitializeInstanceDuration() {
        return initializeInstanceDuration;
    }

    public Integer getSelfProtectedDuration() {
        return selfProtectedDuration;
    }

    public LaunchStatus getStatus() {
        return status;
    }
}
