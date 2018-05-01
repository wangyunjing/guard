package com.wyj.guard.info.loader;


import com.wyj.guard.info.config.InstanceConfig;

/**
 * 实例的配置加载器
 */
public abstract class InstanceConfigLoader implements ConfigurationLoader<InstanceConfig> {

    // 所有应用的配置
    protected AppConfigLoader appConfigLoader;

    public InstanceConfigLoader(AppConfigLoader appConfigLoader) {
        this.appConfigLoader = appConfigLoader;
    }

    // 加载指定应用的所有实例的配置
    public abstract InstanceConfig[] load(String applicationName);

    // 加载指定应用以及指定实例的配置
    public abstract InstanceConfig load(String applicationName, String instanceId);

}
