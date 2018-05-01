package com.wyj.guard.info.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 方便扩展
 */
public abstract class AbstractApplicationConfig implements ConfigurableApplicationConfig {

    public final Logger logger = LoggerFactory.getLogger(AbstractApplicationConfig.class);

    protected int applicationId;

    public AbstractApplicationConfig(int applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public Integer getApplicationId() {
        return applicationId;
    }
}
