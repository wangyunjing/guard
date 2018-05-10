package com.wyj.guard.bootstrap;

import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.DefaultGuardContext;
import com.wyj.guard.context.GuardContext;
import com.wyj.guard.context.GuardProperties;
import com.wyj.guard.context.env.DefaultEnvironment;
import com.wyj.guard.context.event.ApplicationEvent;
import com.wyj.guard.info.ApplicationInfo;
import com.wyj.guard.info.ApplicationInfoSupplier;
import com.wyj.guard.info.InstanceInfo;
import com.wyj.guard.info.InstanceInfoSupplier;
import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.info.loader.AppConfigLoader;
import com.wyj.guard.info.loader.DBAppConfigLoader;
import com.wyj.guard.info.loader.DBInstanceConfigLoader;
import com.wyj.guard.info.loader.InstanceConfigLoader;
import com.wyj.guard.remote.JSCHClient;
import com.wyj.guard.remote.SSHClient;
import com.wyj.guard.web.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.function.Function;

/**
 * 统一启动类
 */
@Component
public class BootStrap implements GuardContext, GuardManagementEndpoint, ApplicationEndpoint, InstanceEndpoint{

    private final Logger logger = LoggerFactory.getLogger(BootStrap.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private GuardProperties guardProperties;

    // 可配置的上下文
    private ConfigurableGuardContext guardContext;

    private AbstractLauncher launcher;

    @PostConstruct
    private void createContext() {
        guardContext = new DefaultGuardContext();

        // 设置环境变量
        guardContext.setEnvironment(new DefaultEnvironment(environment));

        // 设置配置
        guardContext.setGuardProperties(guardProperties);

        // 设置远程连接服务器的类
        guardContext.setSSHClient(new JSCHClient(restTemplate));

        // 设置加载资源的类
        guardContext.setAppConfigLoader(new DBAppConfigLoader(jdbcTemplate));
        guardContext.setInstanceConfigLoader(new DBInstanceConfigLoader(
                guardContext.getAppConfigLoader(), jdbcTemplate));

        // 设置通过配置生成实体的类
        guardContext.setInstanceInfoSupplier(new InstanceInfoSupplier());
        guardContext.setApplicationInfoSupplier(new ApplicationInfoSupplier());

    }

    public boolean launch() {
        launcher = new SingleLauncher(guardContext);
        return launcher.launch();
    }

    @Override
    public boolean addApplication(ApplicationConfig applicationConfig) {
        return launcher.addApplication(applicationConfig);
    }

    @Override
    public boolean removeApplication(Integer applicationId) {
        return launcher.removeApplication(applicationId);
    }

    @Override
    public ApplicationInfo[] queryApplication(ApplicationCondition condition) {
        return launcher.queryApplication(condition);
    }

    @Override
    public ApplicationInfo getApplication(Integer applicationId) {
        return launcher.getApplication(applicationId);
    }

    @Override
    public void refresh(Integer applicationId, String instanceId) {
        launcher.refresh(applicationId, instanceId);
    }

    @Override
    public void close() {
        launcher.close();
    }

    @Override
    public boolean addInstance(InstanceConfig instanceConfig) {
        return launcher.addInstance(instanceConfig);
    }

    @Override
    public boolean removeInstance(Integer applicationId, String instanceId) {
        return launcher.removeInstance(applicationId, instanceId);
    }

    @Override
    public InstanceInfo[] queryInstance(InstanceCondition condition) {
        return launcher.queryInstance(condition);
    }

    @Override
    public InstanceInfo getInstance(Integer applicationId, String instanceId) {
        return launcher.getInstance(applicationId, instanceId);
    }


    @Override
    public com.wyj.guard.context.env.Environment getEnvironment() {
        return guardContext.getEnvironment();
    }

    @Override
    public GuardProperties getGuardProperties() {
        return guardContext.getGuardProperties();
    }

    @Override
    public SSHClient getSSHClient() {
        return guardContext.getSSHClient();
    }

    @Override
    public Function<InstanceConfig, InstanceInfo> getInstanceInfoSupplier() {
        return guardContext.getInstanceInfoSupplier();
    }

    @Override
    public Function<ApplicationConfig, ApplicationInfo> getApplicationInfoSupplier() {
        return guardContext.getApplicationInfoSupplier();
    }

    @Override
    public AppConfigLoader getAppConfigLoader() {
        return guardContext.getAppConfigLoader();
    }

    @Override
    public InstanceConfigLoader getInstanceConfigLoader() {
        return guardContext.getInstanceConfigLoader();
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        guardContext.publishEvent(event);
    }
}
