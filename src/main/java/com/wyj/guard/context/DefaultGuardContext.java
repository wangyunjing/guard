package com.wyj.guard.context;

import com.wyj.guard.context.env.Environment;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.info.loader.AppConfigLoader;
import com.wyj.guard.info.loader.InstanceConfigLoader;
import com.wyj.guard.remote.SSHClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

public class DefaultGuardContext extends AbstractGuardContext {

    private Logger logger = LoggerFactory.getLogger(DefaultGuardContext.class);

    private Environment environment;

    private GuardProperties guardProperties;

    private SSHClient sshClient;

    private Function<InstanceConfig, InstanceInfo> instanceInfoSupplier;

    private Function<ApplicationConfig, ApplicationInfo> applicationInfoSupplier;

    private AppConfigLoader appConfigLoader;

    private InstanceConfigLoader instanceConfigLoader;

    private RestTemplate restTemplate;

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setGuardProperties(GuardProperties guardProperties) {
        this.guardProperties = guardProperties;
    }

    @Override
    public GuardProperties getGuardProperties() {
        return this.guardProperties;
    }

    @Override
    public void setSSHClient(SSHClient sshClient) {
        this.sshClient = sshClient;
    }

    @Override
    public SSHClient getSSHClient() {
        return sshClient;
    }

    @Override
    public void setInstanceInfoSupplier(Function<InstanceConfig, InstanceInfo> supplier) {
        this.instanceInfoSupplier = supplier;
    }

    @Override
    public void setApplicationInfoSupplier(Function<ApplicationConfig, ApplicationInfo> supplier) {
        this.applicationInfoSupplier = supplier;
    }

    @Override
    public Function<InstanceConfig, InstanceInfo> getInstanceInfoSupplier() {
        return this.instanceInfoSupplier;
    }

    @Override
    public Function<ApplicationConfig, ApplicationInfo> getApplicationInfoSupplier() {
        return this.applicationInfoSupplier;
    }

    @Override
    public void setAppConfigLoader(AppConfigLoader appConfigLoader) {
        this.appConfigLoader = appConfigLoader;
    }

    @Override
    public void setInstanceConfigLoader(InstanceConfigLoader instanceConfigLoader) {
        this.instanceConfigLoader = instanceConfigLoader;
    }

    @Override
    public AppConfigLoader getAppConfigLoader() {
        return this.appConfigLoader;
    }

    @Override
    public InstanceConfigLoader getInstanceConfigLoader() {
        return this.instanceConfigLoader;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

}


