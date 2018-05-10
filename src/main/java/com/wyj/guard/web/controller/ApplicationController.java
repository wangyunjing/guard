package com.wyj.guard.web.controller;

import com.wyj.guard.context.GuardContext;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.ConfigurableApplicationConfig;
import com.wyj.guard.info.config.ConfigurableInstanceConfig;
import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.web.Application;
import com.wyj.guard.web.ApplicationCondition;
import com.wyj.guard.web.ApplicationEndpoint;
import com.wyj.guard.web.GuardManagementEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
public class ApplicationController {

    @Autowired
    GuardContext context;

    @Autowired
    GuardManagementEndpoint managementEndpoint;

    @Autowired
    ApplicationEndpoint endpoint;

    @GetMapping("/applications/list")
    public ApplicationInfo[] getApplication(ApplicationCondition condition) {
        return endpoint.queryApplication(condition);
    }

    @GetMapping("/applications/{applicationId}")
    public ApplicationInfo getApplication(@RequestParam("applicationId") Integer applicationId) {
        return endpoint.getApplication(applicationId);
    }

    @PostMapping("/applications")
    public boolean addApplication(@RequestBody Application application) {
        ApplicationConfig applicationConfig = context.getAppConfigLoader().addApplication(application);
        return endpoint.addApplication(applicationConfig);
    }

    @DeleteMapping("/applications/{applicationId}")
    public boolean removeApplication(@RequestParam("applicationId") Integer applicationId) {
        context.getAppConfigLoader().removeApplication(applicationId);
        return endpoint.removeApplication(applicationId);
    }

    @PutMapping("/applications/{applicationId}")
    public void updateApplication(@PathVariable("applicationId") Integer applicationId,
                                  @RequestBody Application application) {
        application.setApplicationId(applicationId);
        context.getAppConfigLoader().updateApplication(application);
        managementEndpoint.refresh(applicationId, null);
    }

    @PutMapping("/applications/{applicationId}/closure")
    public void physicalCloseApplication(@PathVariable("applicationId") Integer applicationId) {
        ApplicationConfig applicationConfig = context.getAppConfigLoader().load(applicationId);
        if (applicationConfig != null) {
            if (applicationConfig instanceof ConfigurableApplicationContext) {
                ConfigurableApplicationConfig configurableApplicationConfig = (ConfigurableApplicationConfig) applicationConfig;
                configurableApplicationConfig.setStatus(LaunchStatus.SHUTDOWN);
                managementEndpoint.refresh(applicationId, null);
            }
        }
    }
}
