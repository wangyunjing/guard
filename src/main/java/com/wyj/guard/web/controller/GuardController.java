package com.wyj.guard.web.controller;

import com.wyj.guard.context.GuardProperties;
import com.wyj.guard.web.GuardManagementEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class GuardController {

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
        managementEndpoint.close();
    }

    @PutMapping("/guard/open")
    public void physicalOpen() {
        managementEndpoint.open();
    }
}
