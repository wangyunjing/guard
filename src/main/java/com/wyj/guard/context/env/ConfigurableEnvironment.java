package com.wyj.guard.context.env;

public interface ConfigurableEnvironment extends Environment{


    void setProperty(String key, String value);

    String setPropertyIfAbsent(String key, String value);
}
