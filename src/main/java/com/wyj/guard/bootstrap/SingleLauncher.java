package com.wyj.guard.bootstrap;


import com.wyj.guard.context.GuardContext;
import com.wyj.guard.info.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 单机启动类
 */
public class SingleLauncher extends AbstractLauncher {

    private Logger logger = LoggerFactory.getLogger(SingleLauncher.class);

    public SingleLauncher(GuardContext guardContext) {
        super(guardContext);
        loadApplications();
    }

    public SingleLauncher(GuardContext guardContext, List<ApplicationManager> applicationManagers) {
        super(guardContext);
        this.applicationManagers = applicationManagers;
    }

    @Override
    public synchronized boolean launch() {
        logger.debug("开始启动单机版...");
        for (int i = 0; i < applicationManagers.size(); i++) {
            if (!applicationManagers.get(i).launch()) {
                logger.debug("启动单机版失败!");
                return false;
            }
        }
        logger.debug("启动单机版成功!");
        launched = true;
        return true;
    }
}
