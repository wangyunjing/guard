package com.wyj.guard.info.loader;


import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.web.Instance;

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

    public abstract InstanceConfig[] load(Integer applicationId);

    // 加载指定应用以及指定实例的配置
    public abstract InstanceConfig load(String applicationName, String instanceId);

    public abstract InstanceConfig load(Integer applicationId, String instanceId);

    public abstract InstanceConfig addInstance(Instance instance);

    public abstract boolean removeInstance(String instanceId);

    public abstract boolean removeInstanceByApplication(Integer applicationId);

    public abstract boolean updateInstance(Instance instance);
}
