package com.wyj.guard.bootstrap;


import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.info.ApplicationManager;
import com.wyj.guard.info.config.ApplicationConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 单机启动类
 */
public class SingleBootStrap {

    // 上下文
    private GuardContext guardContext;

    private List<ApplicationManager> applicationManagers;

    public SingleBootStrap(GuardContext guardContext) {
        this.guardContext = guardContext;
    }

    private volatile boolean launched = false;

    public boolean launch() {
        refresh();
        launched = true;
        return true;
    }

    private void refresh() {

        // 生成应用管理器
        applicationManagers = new ArrayList<>();

        for (ApplicationConfig applicationConfig : guardContext.getAppConfigLoader().load()) {
            ApplicationManager applicationManager = new ApplicationManager(
                    (ConfigurableGuardContext) guardContext,
                    applicationConfig);
            applicationManagers.add(applicationManager);
        }

        for (ApplicationManager applicationManager : applicationManagers) {
            boolean launch = applicationManager.launch();
        }
    }

    // 是否启动
    public boolean isLaunched() {
        return launched;
    }
}
