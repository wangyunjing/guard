package com.wyj.guard.info.loader;


import com.wyj.guard.info.config.ApplicationConfig;

/**
 * 应用的配置加载器
 */
public abstract class AppConfigLoader implements ConfigurationLoader<ApplicationConfig> {

    public abstract ApplicationConfig load(String applicationName);

    public abstract ApplicationConfig load(Integer applicationId);

}
