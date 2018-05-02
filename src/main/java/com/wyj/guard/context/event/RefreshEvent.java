package com.wyj.guard.context.event;

import com.wyj.guard.context.GuardContext;

/**
 * 刷新配置事件
 */
public class RefreshEvent extends ApplicationEvent<GuardContext> {
    public RefreshEvent(GuardContext context) {
        super(context);
    }
}
