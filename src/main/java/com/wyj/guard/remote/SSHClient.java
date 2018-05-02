package com.wyj.guard.remote;


import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.share.enums.InstanceStatus;
import com.wyj.guard.share.enums.ServerStatus;

/**
 * 远程连接服务器并且启动实例
 */
public interface SSHClient {

    // 启动实例
    boolean startInstance(InstanceInfo instanceInfo);

    // 关闭实例
    boolean closeInstance(InstanceInfo instanceInfo);

    // 获取实例状态
    InstanceStatus getInstanceStatus(InstanceInfo instanceInfo);

    ServerStatus getServerStatus(InstanceInfo instanceInfo);
}
