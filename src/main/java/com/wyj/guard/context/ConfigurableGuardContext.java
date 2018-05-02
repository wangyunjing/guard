package com.wyj.guard.context;

import com.wyj.guard.context.env.Environment;
import com.wyj.guard.context.event.ApplicationListener;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.info.loader.AppConfigLoader;
import com.wyj.guard.info.loader.InstanceConfigLoader;
import com.wyj.guard.remote.SSHClient;

import java.util.function.Function;

public interface ConfigurableGuardContext extends GuardContext {

    void setEnvironment(Environment environment);

    void setGuardProperties(GuardProperties guardProperties);

//    void setRedisTemplate(RedisTemplate redisTemplate);

    void setSSHClient(SSHClient sshClient);

    void setInstanceInfoSupplier(Function<InstanceConfig, InstanceInfo> supplier);

    void setApplicationInfoSupplier(Function<ApplicationConfig, ApplicationInfo> supplier);

    void setAppConfigLoader(AppConfigLoader appConfigLoader);

    void setInstanceConfigLoader(InstanceConfigLoader instanceConfigLoader);

    void addApplicationListener(ApplicationListener<?> listener);

//    void addRefreshableConfig(Refreshable refreshableConfig);
//
//    void addRefreshableConfigs(Refreshable[] refreshableConfigs);
//
//    void setRefreshableConfigs(Refreshable[] refreshableConfigs);
}
