package com.wyj.guard.context.env;

import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public class DefaultEnvironment implements ConfigurableEnvironment {

    // Spring中的环境
    private Environment environment;

    private Map<String, String> map = new HashMap<>();

    public DefaultEnvironment(Environment environment) {
        this.environment = environment;
    }


    public void setProperty(String key, String value) {
        map.put(key, value);
    }

    @Override
    public String setPropertyIfAbsent(String key, String value) {

        if (map.get(key) != null) {
            return map.get(key);
        }

        String property = environment.getProperty(key);
        if (property != null) {
            return property;
        }
        return null;
    }

    @Override
    public boolean containsProperty(String key) {
        return map.containsKey(key) || environment.containsProperty(key);
    }

    @Override
    public String getProperty(String key) {
        if (map.get(key) != null) {
            return map.get(key);
        }
        return environment.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        if (map.get(key) != null) {
            return map.get(key);
        }
        if (environment.getProperty(key) != null) {
            return environment.getProperty(key);
        }
        return defaultValue;
    }

    @Override
    public <T> T getProperty(String key, Class<T> clazz) {
        // TODO: 2018/2/5 暂不支持 新加入的属性转换;
        return environment.getProperty(key, clazz);
    }

    @Override
    public <T> T getProperty(String key, Class<T> clazz, T defaultValue) {
        // TODO: 2018/2/5 暂不支持 新加入的属性转换
        return environment.getProperty(key, clazz, defaultValue);
    }
}
