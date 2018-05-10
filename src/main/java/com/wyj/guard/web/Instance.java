package com.wyj.guard.web;

public class Instance {

    /**
     * 所属应用的ID
     */
    private Integer applicationId;

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

    /**
     * 状态 {@link com.wyj.guard.share.enums.LaunchStatus}
     */
    private Short status;

    // 启动状态 {"虚拟关闭", "物理关闭", "启动中"}
    private String launchStatus;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHealthUrl() {
        return healthUrl;
    }

    public void setHealthUrl(String healthUrl) {
        this.healthUrl = healthUrl;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getStartCommand() {
        return startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getHeartbeatRate() {
        return heartbeatRate;
    }

    public void setHeartbeatRate(Integer heartbeatRate) {
        this.heartbeatRate = heartbeatRate;
    }

    public Integer getInitializeInstanceDuration() {
        return initializeInstanceDuration;
    }

    public void setInitializeInstanceDuration(Integer initializeInstanceDuration) {
        this.initializeInstanceDuration = initializeInstanceDuration;
    }

    public Integer getSelfProtectedDuration() {
        return selfProtectedDuration;
    }

    public void setSelfProtectedDuration(Integer selfProtectedDuration) {
        this.selfProtectedDuration = selfProtectedDuration;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getLaunchStatus() {
        return launchStatus;
    }

    public void setLaunchStatus(String launchStatus) {
        this.launchStatus = launchStatus;
    }
}
