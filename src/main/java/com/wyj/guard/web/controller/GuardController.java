package com.wyj.guard.web.controller;

import com.wyj.guard.web.GuardManagementEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuardController {

    @Autowired
    GuardManagementEndpoint managementEndpoint;

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
