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

    // 设置环境变量
    void setEnvironment(Environment environment);

    // 设置系统配置
    void setGuardProperties(GuardProperties guardProperties);

    // 设置远程连接工具
    void setSSHClient(SSHClient sshClient);

    // 设置实例信息生成器
    void setInstanceInfoSupplier(Function<InstanceConfig, InstanceInfo> supplier);

    // 设置应用信息生成器
    void setApplicationInfoSupplier(Function<ApplicationConfig, ApplicationInfo> supplier);

    // 设置应用配置加载器
    void setAppConfigLoader(AppConfigLoader appConfigLoader);

    // 设置实例配置加载器
    void setInstanceConfigLoader(InstanceConfigLoader instanceConfigLoader);

    // 添加应用监听器
    void addApplicationListener(ApplicationListener<?> listener);

    // 移除应用监听器
    void removeApplicationListener(ApplicationListener<?> listener);
}
