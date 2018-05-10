package com.wyj.guard.context.event;

import com.wyj.guard.context.GuardContext;

/**
 * 刷新配置事件
 */
public class RefreshEvent extends ApplicationEvent<GuardContext> {

    private Integer applicationId;

    private String instanceId;

    public RefreshEvent(GuardContext context, Integer applicationId, String instanceId) {
        super(context);
        this.applicationId = applicationId;
        this.instanceId = instanceId;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
