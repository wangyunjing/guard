package com.wyj.guard.context.event;

import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.share.enums.InstanceStatus;

/**
 * 实例关闭时间
 */
public class CloseInstanceEvent extends ApplicationEvent {

    // 应用名称
    private String applicationName;

    // 实例ID
    private InstanceInfo instanceInfo;

    // 实例状态
    private InstanceStatus instanceStatus;

    public CloseInstanceEvent(String applicationName,
                              InstanceInfo instanceInfo,
                              InstanceStatus instanceStatus) {
        super(instanceStatus);
        this.applicationName = applicationName;
        this.instanceInfo = instanceInfo;
        this.instanceStatus = instanceStatus;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }
}
