package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;

/**
 * 可配置的应用配置
 */
public interface ConfigurableApplicationConfig extends ApplicationConfig {

    // 设置默认健康检查URL
    void setDefaultHealthUrl(String healthUrl);

    // 设置默认启动命令
    void setDefaultStartCommand(String startCommand);

    // 设置启动实例的个数
    void setStartInstanceNum(Integer startInstanceNum);

    // 设置远程服务器用户名
    void setDefaultUsername(String username);

    // 设置远程服务器密码
    void setDefaultPassword(String password);

    // 设置监控服务器可用的周期（毫秒）
    void setMonitorServerAvailableDuration(Integer duration);

    // 设置监控实例个数的周期(毫秒)
    void setMonitoringInstanceNumDuration(Integer duration);

    // 设置默认心跳检查频率(毫秒)
    void setDefaultHeartbeatRate(Integer rate);

    // 设置默认初始化实例时长(毫秒)
    void setDefaultInitializeInstanceDuration(Integer duration);

    // 设置默认自我保护周期(毫秒)
    void setDefaultSelfProtectedDuration(Integer duration);

    // 设置状态
    void setStatus(LaunchStatus launchStatus);
}
