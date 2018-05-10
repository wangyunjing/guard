package com.wyj.guard.info;


import com.wyj.guard.info.config.InstanceConfig;

import java.util.function.Function;

/**
 * {@link InstanceInfo} 生成器
 */
public class InstanceInfoSupplier implements Function<InstanceConfig, InstanceInfo> {

    @Override
    public InstanceInfo apply(InstanceConfig instanceConfig) {
        InstanceInfo instanceInfo = InstanceInfo.Builder.newBuilder()
                .setApplicationId(instanceConfig.getApplicationId())
                .setApplicationName(instanceConfig.getApplicationName())
                .setInstanceId(instanceConfig.getInstanceId())
                .setIp(instanceConfig.getIp())
                .setPort(instanceConfig.getPort())
                .setStartCommand(instanceConfig.getStartCommand())
                .setHealthUrl(instanceConfig.getHealthUrl())
                .setWeight(instanceConfig.getWeight())
                .setUsername(instanceConfig.getUsername())
                .setPassword(instanceConfig.getPassword())
                .setHeartbeatRate(instanceConfig.getHeartbeatRate())
                .setInitializeInstanceDuration(instanceConfig.getInitializeInstanceDuration())
                .setSelfProtectedDuration(instanceConfig.getSelfProtectedDuration())
                .setStatus(instanceConfig.getStatus())
                .build();

        return instanceInfo;
    }
}
