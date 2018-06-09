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

    // 获取环境变量
    Environment getEnvironment();

    // 获取系统配置
    GuardProperties getGuardProperties();

    // 获取远程连接工具
    SSHClient getSSHClient();

    // 获取实例信息生成器
    Function<InstanceConfig, InstanceInfo> getInstanceInfoSupplier();

    // 获取应用信息生成器
    Function<ApplicationConfig, ApplicationInfo> getApplicationInfoSupplier();

    // 获取应用配置加载器
    AppConfigLoader getAppConfigLoader();

    // 获取实例配置加载器
    InstanceConfigLoader getInstanceConfigLoader();
}
