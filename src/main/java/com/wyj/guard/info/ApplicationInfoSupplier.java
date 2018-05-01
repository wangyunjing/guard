package com.wyj.guard.info;


import com.wyj.guard.info.config.ApplicationConfig;

import java.util.function.Function;

/**
 * {@link ApplicationInfo} 生成器
 */
public class ApplicationInfoSupplier implements Function<ApplicationConfig, ApplicationInfo> {


    @Override
    public ApplicationInfo apply(ApplicationConfig applicationConfig) {
        return ApplicationInfo.Builder.newBuilder()
                .setApplicationId(applicationConfig.getApplicationId())
                .setApplicationName(applicationConfig.getApplicationName())
                .setDefaultPort(applicationConfig.getDefaultPort())
                .setDefaultHealthUrl(applicationConfig.getDefaultHealthUrl())
                .setDefaultStartCommand(applicationConfig.getDefaultStartCommand())
                .setStartInstanceNum(applicationConfig.getStartInstanceNum())
                .setDefaultUsername(applicationConfig.getDefaultUsername())
                .setDefaultPassword(applicationConfig.getDefaultPassword())
                .setMonitorServerAvailableDuration(applicationConfig.getMonitorServerAvailableDuration())
                .setMonitoringInstanceNumDuration(applicationConfig.getMonitoringInstanceNumDuration())
                .setDefaultHeartbeatRate(applicationConfig.getDefaultHeartbeatRate())
                .setDefaultInitializeInstanceDuration(applicationConfig.getDefaultInitializeInstanceDuration())
                .setDefaultSelfProtectedDuration(applicationConfig.getDefaultSelfProtectedDuration())
                .setStatus(applicationConfig.getStatus())
                .build();
    }

}
