package com.wyj.guard.web;

import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.config.ApplicationConfig;

public interface ApplicationEndpoint {

    /**
     * 添加应用
     */
    boolean addApplication(ApplicationConfig applicationConfig);

    /**
     * 移除应用
     */
    boolean removeApplication(Integer applicationId);

    /**
     * 查询应用
     */
    Application[] queryApplication(ApplicationCondition condition);

    /**
     * 获取应用
     */
    ApplicationInfo getApplication(Integer applicationId);

}
