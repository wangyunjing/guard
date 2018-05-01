package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;

public interface ApplicationConfig {

    // 应用ID
    Integer getApplicationId();

    // 应用名称
    String getApplicationName();

    // 实例启动的默认端口
    Integer getDefaultPort();

    // 默认健康检查URL
    String getDefaultHealthUrl();

    // 默认启动命令
    String getDefaultStartCommand();

    // 启动实例的个数
    Integer getStartInstanceNum();

    // 默认远程服务器用户名
    String getDefaultUsername();

    // 默认远程服务器密码
    String getDefaultPassword();

    // 监控服务器可用的周期（毫秒）
    Integer getMonitorServerAvailableDuration();

    // 监控实例个数的周期(毫秒)
    Integer getMonitoringInstanceNumDuration();

    // 默认心跳检查频率(毫秒)
    Integer getDefaultHeartbeatRate();

    // 默认初始化实例时长(毫秒)
    Integer getDefaultInitializeInstanceDuration();

    // 默认自我保护周期(毫秒)
    Integer getDefaultSelfProtectedDuration();

    // 状态
    LaunchStatus getStatus();
}
