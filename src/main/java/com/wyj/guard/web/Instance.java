package com.wyj.guard.web;

import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.InstanceManager;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Instance {

    /**
     * 所属应用的ID
     */
    @NotNull(message = "应用ID不能为空")
    private Integer applicationId;

    // 所属应用名
    private String applicationName;

    // 实例名
    private String instanceId;

    // ip地址
    @NotEmpty(message = "IP不能为空")
    private String ip;

    // 应用启动端口号
    @NotNull(message = "端口号不能为空")
    private Integer port;

    // 应用健康检查URL
    @NotEmpty(message = "健康检测URI不能为空")
    private String healthUrl;

    // 权重（用于启动实例）
    @NotNull(message = "权重不能为空")
    private Integer weight;

    // 启动命令
    @NotEmpty(message = "启动命令不能为空")
    private String startCommand;

    // 远程服务器用户名
    @NotEmpty(message = "用户名不能为空")
    private String username;

    // 远程服务器密码
    @NotEmpty(message = "密码不能为空")
    private String password;

    // 心跳频率（毫秒）
    @NotNull
    @Min(2000)
    private Integer heartbeatRate;

    // 初始化实例时长
    @NotNull
    @Min(4000)
    private Integer initializeInstanceDuration;

    // 自我保护的时长（出现第一次down时开始计算）
    @NotNull
    @Min(6000)
    private Integer selfProtectedDuration;

    /**
     * 状态 {@link com.wyj.guard.share.enums.LaunchStatus}
     */
    @NotNull
    private Short status;

    // 启动状态 {"虚拟关闭", "物理关闭", "启动中"}
    private Short launchStatus;

    public static Instance build(InstanceManager instanceManager) {
        Instance instance = new Instance();
        InstanceInfo instanceInfo = instanceManager.getInstanceInfo();
        instance.setApplicationId(instanceInfo.getApplicationId());
        instance.setApplicationName(instanceInfo.getApplicationName());
        instance.setInstanceId(instanceInfo.getInstanceId());
        instance.setIp(instanceInfo.getIp());
        instance.setPort(instanceInfo.getPort());
        instance.setHealthUrl(instanceInfo.getHealthUrl());
        instance.setWeight(instanceInfo.getWeight());
        instance.setStartCommand(instanceInfo.getStartCommand());
        instance.setUsername(instanceInfo.getUsername());
        instance.setPassword(instanceInfo.getPassword());
        instance.setHeartbeatRate(instanceInfo.getHeartbeatRate());
        instance.setInitializeInstanceDuration(instanceInfo.getInitializeInstanceDuration());
        instance.setSelfProtectedDuration(instanceInfo.getSelfProtectedDuration());
        instance.setStatus(instanceInfo.getStatus().status);
        if (instanceManager.isVirtualClosed() || instanceManager.isPhysicalClosed()) {
            instance.setLaunchStatus((short) 0);
        } else {
            instance.setLaunchStatus((short) 1);
        }
        return instance;
    }

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

    public Short getLaunchStatus() {
        return launchStatus;
    }

    public void setLaunchStatus(Short launchStatus) {
        this.launchStatus = launchStatus;
    }
}
