package com.wyj.guard.bootstrap;

import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.event.RefreshEvent;
import com.wyj.guard.web.ApplicationEndpoint;
import com.wyj.guard.web.GuardManagementEndpoint;
import com.wyj.guard.web.InstanceEndpoint;

public abstract class AbstractLauncher implements Launcher, GuardManagementEndpoint,
        ApplicationEndpoint, InstanceEndpoint {

    // 上下文
    protected GuardContext guardContext;

    protected volatile boolean launched = false;

    public AbstractLauncher(GuardContext guardContext) {
        this.guardContext = guardContext;
    }

    // 是否启动
    @Override
    public boolean isLaunched() {
        return launched;
    }

    @Override
    public void refresh(Integer applicationId, String instanceId) {
        RefreshEvent event = new RefreshEvent(guardContext, applicationId, instanceId);
        guardContext.publishEvent(event);
    }
}
