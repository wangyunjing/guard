package com.wyj.guard.web;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class Application {

    // 应用Id
    private Integer applicationId;

    // 应用名称
    @NotEmpty(message = "应用名不能为空")
    private String applicationName;

    // 默认启动端口号
    private Integer port;

    // 默认健康检查URL
    private String healthUrl;

    // 默认启动命令
    private String startCommand;

    // 启动实例的个数
    @NotNull
    @Min(1)
    private Integer startInstanceNum;

    // 默认远程服务器的用户名
    private String username;

    // 默认远程服务器的密码
    private String password;

    // 维护实例的周期
    @NotNull(message = "维护实例的周期不能为空")
    @Min(3000)
    private Integer defendInstanceDuration;

    // 默认心跳检查频率(毫秒)
    @Min(2000)
    private Integer heartbeatRate;

    // 默认初始化实例时长(毫秒)
    @Min(4000)
    private Integer initializeInstanceDuration;

    // 默认自我保护周期(毫秒)
    @Min(6000)
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

    public String getStartCommand() {
        return startCommand;
    }

    public void setStartCommand(String startCommand) {
        this.startCommand = startCommand;
    }

    public Integer getStartInstanceNum() {
        return startInstanceNum;
    }

    public void setStartInstanceNum(Integer startInstanceNum) {
        this.startInstanceNum = startInstanceNum;
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

    public Integer getDefendInstanceDuration() {
        return defendInstanceDuration;
    }

    public void setDefendInstanceDuration(Integer defendInstanceDuration) {
        this.defendInstanceDuration = defendInstanceDuration;
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
