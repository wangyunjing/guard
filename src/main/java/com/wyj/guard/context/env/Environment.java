package com.wyj.guard.context.env;

public interface Environment {


    boolean containsProperty(String key);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> clazz);

    <T> T getProperty(String key, Class<T> clazz, T defaultValue);

}
