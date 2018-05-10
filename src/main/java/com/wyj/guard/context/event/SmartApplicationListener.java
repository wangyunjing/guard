package com.wyj.guard.context.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class SmartApplicationListener implements ApplicationListener<ApplicationEvent> {

    private static Logger logger = LoggerFactory.getLogger(SmartApplicationListener.class);

    private ApplicationListener applicationListener;

    public SmartApplicationListener(ApplicationListener applicationListener) {
        this.applicationListener = applicationListener;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        applicationListener.onApplicationEvent(event);
    }

    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        Class type = getEventType();
        return type.isAssignableFrom(eventType);
    }

    public Class getEventType() {
        try {
            Type[] genericInterfaces = applicationListener.getClass().getGenericInterfaces();
            Class type = (Class) ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
            return type;
        } catch (Exception e) {
            logger.debug("{}没有找到泛型的Class！默认返回ApplicationEvent.class",
                    applicationListener.getClass());
        }
        return ApplicationEvent.class;
    }
}
