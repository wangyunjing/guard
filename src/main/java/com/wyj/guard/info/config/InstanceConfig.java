package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;

/**
 * 实例配置
 */
public interface InstanceConfig {

    // 获取应用ID
    Integer getApplicationId();

    // 获取应用名
    String getApplicationName();

    // 应用实例ID
    String getInstanceId();

    // 实例所在的服务器IP
    String getIp();

    // 实例启动的端口号
    Integer getPort();

    // 实例启动的命令
    String getStartCommand();

    // 健康检测URL
    String getHealthUrl();

    // 权重
    Integer getWeight();

    // 实例所在服务器，登陆用户名
    String getUsername();

    // 实例所在服务器，登陆密码
    String getPassword();

    // 心跳检查频率(毫秒)
    Integer getHeartbeatRate();

    // 初始化实例时长(毫秒)
    Integer getInitializeInstanceDuration();

    // 自我保护周期(毫秒)
    Integer getSelfProtectedDuration();

    // 状态
    LaunchStatus getStatus();
}
