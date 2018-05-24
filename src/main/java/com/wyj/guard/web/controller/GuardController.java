package com.wyj.guard.web.controller;

import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.GuardProperties;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.ConfigurableApplicationConfig;
import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.web.GuardManagementEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GuardController {

    @Autowired
    GuardContext context;

    @Autowired
    GuardManagementEndpoint managementEndpoint;

    @Autowired
    GuardProperties guardProperties;

    @GetMapping("/guard/is_cloud")
    public boolean isCloud() {
        return guardProperties.isWhetherCluster();
    }

    @PostMapping("/guard/refresh")
    public void refreshConfig(@RequestParam(value = "applicationId", required = false) Integer applicationId,
                              @RequestParam(value = "instanceId", required = false) String instanceId) {
        managementEndpoint.refresh(applicationId, instanceId);
    }

    @PutMapping("/guard/closure")
    public void physicalClose() {
        ApplicationConfig[] applicationConfigs = context.getAppConfigLoader().load();
        if (applicationConfigs != null) {
            for (ApplicationConfig applicationConfig : applicationConfigs) {
                if (applicationConfig != null) {
                    if (applicationConfig instanceof ConfigurableApplicationConfig) {
                        ConfigurableApplicationConfig configurableApplicationConfig = (ConfigurableApplicationConfig) applicationConfig;
                        configurableApplicationConfig.setStatus(LaunchStatus.SHUTDOWN);
                    }
                }
            }
        }
        managementEndpoint.refresh(null, null);
    }

    @PutMapping("/guard/open")
    public void physicalOpen() {
        ApplicationConfig[] applicationConfigs = context.getAppConfigLoader().load();
        if (applicationConfigs != null) {
            for (ApplicationConfig applicationConfig : applicationConfigs) {
                if (applicationConfig != null) {
                    if (applicationConfig instanceof ConfigurableApplicationConfig) {
                        ConfigurableApplicationConfig configurableApplicationConfig = (ConfigurableApplicationConfig) applicationConfig;
                        configurableApplicationConfig.setStatus(LaunchStatus.UP);
                    }
                }
            }
        }
        managementEndpoint.refresh(null, null);
    }
}
