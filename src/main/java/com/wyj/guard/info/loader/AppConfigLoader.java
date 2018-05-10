package com.wyj.guard.info.loader;


import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.web.Application;

/**
 * 应用的配置加载器
 */
public abstract class AppConfigLoader implements ConfigurationLoader<ApplicationConfig> {

    public abstract ApplicationConfig load(String applicationName);

    public abstract ApplicationConfig load(Integer applicationId);

    public abstract ApplicationConfig addApplication(Application application);

    public abstract boolean removeApplication(Integer applicationId);

    public abstract boolean updateApplication(Application application);

}
