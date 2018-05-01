package com.wyj.guard.context;


import com.wyj.guard.context.env.Environment;
import com.wyj.guard.context.event.ApplicationEventPublisher;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.info.loader.AppConfigLoader;
import com.wyj.guard.info.loader.InstanceConfigLoader;
import com.wyj.guard.remote.SSHClient;

import java.util.function.Function;

public interface GuardContext extends ApplicationEventPublisher {

    Environment getEnvironment();

    GuardProperties getGuardProperties();

//    DistributedLock getDistributedLock(String key);
//
//    DistributedLock getDistributedLock(String key, int expireMS);

    SSHClient getSSHClient();

    Function<InstanceConfig, InstanceInfo> getInstanceInfoSupplier();

    Function<ApplicationConfig, ApplicationInfo> getApplicationInfoSupplier();

    AppConfigLoader getAppConfigLoader();

    InstanceConfigLoader getInstanceConfigLoader();

//    Refreshable[] getRefreshConfigs();
}
