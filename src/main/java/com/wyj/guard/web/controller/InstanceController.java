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

    @GetMapping("/applications/{applicationId}/instances/{instanceId}")
    public InstanceInfo getInstance(@RequestParam("applicationId") Integer applicationId,
                                    @RequestParam("instanceId") String instanceId) {
        return instanceEndpoint.getInstance(applicationId, instanceId);
    }

    @PutMapping("/applications/{applicationId}/instances/{instanceId}")
    public void updateInstance(@PathVariable("applicationId") Integer applicationId,
                                 @PathVariable("instanceId") String instanceId,
                                 @RequestBody Instance instance) {
        instance.setApplicationId(applicationId);
        instance.setInstanceId(instanceId);
        context.getInstanceConfigLoader().updateInstance(instance);
        managementEndpoint.refresh(applicationId, instanceId);
    }

    @PostMapping("/applications/{applicationId}/instances")
    public boolean addInstance(@PathVariable("applicationId") Integer applicaionId,
                              @RequestBody Instance instance) {
        instance.setApplicationId(applicaionId);
        instance.setInstanceId(instance.getIp() + ":" + instance.getPort());
        InstanceConfig instanceConfig = context.getInstanceConfigLoader().addInstance(instance);
        return instanceEndpoint.addInstance(instanceConfig);
    }

    @DeleteMapping("/applications/{applicationId}/instances/{instanceId}")
    public boolean removeInstance(@PathVariable("applicationId") Integer applicaionId,
                                 @PathVariable("instanceId") String instanceId) {
        context.getInstanceConfigLoader().removeInstance(instanceId);
        return instanceEndpoint.removeInstance(applicaionId, instanceId);
    }

    @PutMapping("/applications/{applicationId}/instances/{instanceId}/closure")
    public void physicalCloseInstance(@PathVariable("applicationId") Integer applicationId,
                                      @PathVariable("instanceId") String instanceId) {
        InstanceConfig instanceConfig = context.getInstanceConfigLoader().load(applicationId, instanceId);
        if (instanceConfig != null) {
            if (instanceConfig instanceof ConfigurableInstanceConfig) {
                ConfigurableInstanceConfig configurableInstanceConfig = (ConfigurableInstanceConfig) instanceConfig;
                configurableInstanceConfig.setStatus(LaunchStatus.SHUTDOWN);
                managementEndpoint.refresh(applicationId, instanceId);
            }
        }
    }

}
