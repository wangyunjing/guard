package com.wyj.guard.info.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方便扩展
 */
public abstract class AbstractInstanceConfig implements ConfigurableInstanceConfig{

    public final Logger logger = LoggerFactory.getLogger(AbstractInstanceConfig.class);

    // 当前实例所属应用的配置
    protected ApplicationConfig applicationConfig;

    // 当前实例ID
    protected String instanceId;

    public AbstractInstanceConfig(ApplicationConfig applicationConfig, String instanceId) {
        this.applicationConfig = applicationConfig;
        this.instanceId = instanceId;
    }


}
