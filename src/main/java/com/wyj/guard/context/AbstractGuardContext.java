package com.wyj.guard.context;

import com.wyj.guard.context.event.ApplicationEvent;
import com.wyj.guard.context.event.ApplicationListener;
import com.wyj.guard.context.event.SmartApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractGuardContext implements ConfigurableGuardContext {

    private Logger logger = LoggerFactory.getLogger(AbstractGuardContext.class);

    // 监听器集合
    private Set<ApplicationListener> applicationListeners = new LinkedHashSet<>();

    // 监听器缓存Map
    private Map<Class, List<SmartApplicationListener>> listenerCacheMap =
            new ConcurrentHashMap<>();

    @Override
    public final void publishEvent(ApplicationEvent event) {
        Class<? extends ApplicationEvent> eventClass = event.getClass();
        List<SmartApplicationListener> smartApplicationListeners = listenerCacheMap.get(eventClass);
        if (ObjectUtils.isEmpty(smartApplicationListeners)) {
            smartApplicationListeners = applicationListeners.stream()
                    .map(applicationListener -> new SmartApplicationListener(applicationListener))
                    .filter(smartApplicationListener ->
                            smartApplicationListener.supportsEventType(eventClass) ||
                                    smartApplicationListener.getEventType().equals(ApplicationEvent.class))
                    .collect(Collectors.toList());
            listenerCacheMap.put(eventClass, smartApplicationListeners);
        }
        smartApplicationListeners.forEach(smartApplicationListener ->
                smartApplicationListener.onApplicationEvent(event));
    }

    @Override
    public final void addApplicationListener(ApplicationListener<?> listener) {
        // 清空缓存
        this.listenerCacheMap.clear();
        this.applicationListeners.add(listener);
    }

}
