package com.wyj.guard.web.controller;

import com.wyj.guard.web.CloudEndpoint;
import com.wyj.guard.web.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudController {

    @Autowired
    CloudEndpoint cloudEndpoint;

    @GetMapping("/cloud/instances/list")
    public Instance[] getInstance() {
        return cloudEndpoint.getCloudInstance();
    }

}
