package com.wyj.guard.share.enums;

/**
 * 实例的状态
 */
public enum InstanceStatus {
    UP, // 正常 : 实例启动，正常提供服务 进程存在
    DOWN, // 宕机 : 实例启动，无法提供服务 进程存在，但不可访问
    SERVER_DOWN, // 服务器不可用 : 无法连接服务器，不可判定实例是否启动
    SHUTDOWN // 关闭 : 实例没有启动，进程不存在
}
