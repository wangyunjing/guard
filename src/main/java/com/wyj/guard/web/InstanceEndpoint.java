package com.wyj.guard.web;

import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.InstanceConfig;

public interface InstanceEndpoint {

    /**
     * 添加实例
     */
    boolean addInstance(InstanceConfig instanceConfig);

    /**
     * 移除实例
     */
    boolean removeInstance(Integer applicationId, String instanceId);

    /**
     * 查询实例
     */
    InstanceInfo[] queryInstance(InstanceCondition condition);

    /**
     * 获取实例
     */
    InstanceInfo getInstance(Integer applicationId, String instanceId);

}
