package com.wyj.guard.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuardController {

    @PostMapping("/guard/refresh")
    public void refreshConfig(@RequestParam(value = "applicationId", required = false) Integer applicationId,
                              @RequestParam(value = "instanceId", required = false) String instanceId) {

    }

    @PutMapping("/guard/closure")
    public void physicalClose() {

    }

}
