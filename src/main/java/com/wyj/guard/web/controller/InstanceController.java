package com.wyj.guard.web.controller;

import com.wyj.guard.context.GuardContext;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.ConfigurableInstanceConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.web.GuardManagementEndpoint;
import com.wyj.guard.web.Instance;
import com.wyj.guard.web.InstanceCondition;
import com.wyj.guard.web.InstanceEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class InstanceController {

    @Autowired
    GuardContext context;

    @Autowired
    GuardManagementEndpoint managementEndpoint;

    @Autowired
    InstanceEndpoint instanceEndpoint;

    @GetMapping("/instances/list")
    public InstanceInfo[] getInstance(InstanceCondition condition) {
        return instanceEndpoint.queryInstance(condition);
    }

    @GetMapping("/applications/{applicationId}/instances")
    public InstanceInfo getInstance(@PathVariable("applicationId") Integer applicationId,
                                    @RequestParam("instanceId") String instanceId) {
        return instanceEndpoint.getInstance(applicationId, instanceId);
    }

    @PutMapping("/applications/{applicationId}/instances")
    public void updateInstance(@PathVariable("applicationId") Integer applicationId,
                                 @RequestParam("instanceId") String instanceId,
                                 @RequestBody @Valid Instance instance) {
        instance.setApplicationId(applicationId);
        instance.setInstanceId(instanceId);
        context.getInstanceConfigLoader().updateInstance(instance);
        managementEndpoint.refresh(applicationId, instanceId);
    }

    @PostMapping("/applications/{applicationId}/instances")
    public boolean addInstance(@PathVariable("applicationId") Integer applicationId,
                              @RequestBody @Valid Instance instance) {
        instance.setApplicationId(applicationId);
        instance.setInstanceId(instance.getIp() + ":" + instance.getPort());
        InstanceConfig instanceConfig = context.getInstanceConfigLoader().addInstance(instance);
        return instanceEndpoint.addInstance(instanceConfig);
    }

    @DeleteMapping("/applications/{applicationId}/instances")
    public boolean removeInstance(@PathVariable("applicationId") Integer applicationId,
                                 @RequestParam("instanceId") String instanceId) {
        context.getInstanceConfigLoader().removeInstance(instanceId);
        return instanceEndpoint.removeInstance(applicationId, instanceId);
    }

    @PutMapping("/applications/{applicationId}/instances/closure")
    public void physicalCloseInstance(@PathVariable("applicationId") Integer applicationId,
                                      @RequestParam("instanceId") String instanceId) {
        InstanceConfig instanceConfig = context.getInstanceConfigLoader().load(applicationId, instanceId);
        if (instanceConfig != null) {
            if (instanceConfig instanceof ConfigurableInstanceConfig) {
                ConfigurableInstanceConfig configurableInstanceConfig = (ConfigurableInstanceConfig) instanceConfig;
                configurableInstanceConfig.setStatus(LaunchStatus.SHUTDOWN);
                managementEndpoint.refresh(applicationId, instanceId);
            }
        }
    }

    @PutMapping("/applications/{applicationId}/instances/open")
    public void physicalOpenInstance(@PathVariable("applicationId") Integer applicationId,
                                      @RequestParam("instanceId") String instanceId) {
        InstanceConfig instanceConfig = context.getInstanceConfigLoader().load(applicationId, instanceId);
        if (instanceConfig != null) {
            if (instanceConfig instanceof ConfigurableInstanceConfig) {
                ConfigurableInstanceConfig configurableInstanceConfig = (ConfigurableInstanceConfig) instanceConfig;
                configurableInstanceConfig.setStatus(LaunchStatus.UP);
                managementEndpoint.refresh(applicationId, instanceId);
            }
        }
    }

}
