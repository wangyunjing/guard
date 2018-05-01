package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;

/**
 * 可配置的应用实例配置
 */
public interface ConfigurableInstanceConfig extends InstanceConfig {

    // 设置健康检查URL
    void setHealthUrl(String healthUrl);

    // 设置权重
    void setWeight(Integer weight);

    // 设置启动命令
    void setStartCommand(String startCommand);

    // 设置远程服务器的用户名
    void setUsername(String username);

    // 设置远程服务的密码
    void setPassword(String password);

    // 设置心跳频率
    void setHeartbeatRate(Integer heartbeatRate);

    // 设置初始化实例周期
    void setInitializeInstanceDuration(Integer duration);

    //设置自我保护的周期
    void setSelfProtectedDuration(Integer duration);

    // 设置状态
    void setStatus(LaunchStatus launchStatus);
}
