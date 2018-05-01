package com.wyj.guard.context.event;

public interface ApplicationEventPublisher {

    void publishEvent(ApplicationEvent event);
}
