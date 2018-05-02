package com.wyj.guard.bootstrap;

import com.wyj.guard.context.ConfigurableGuardContext;
import com.wyj.guard.context.DefaultGuardContext;
import com.wyj.guard.context.GuardProperties;
import com.wyj.guard.context.env.DefaultEnvironment;
import com.wyj.guard.info.ApplicationInfoSupplier;
import com.wyj.guard.info.InstanceInfoSupplier;
import com.wyj.guard.info.loader.DBAppConfigLoader;
import com.wyj.guard.info.loader.DBInstanceConfigLoader;
import com.wyj.guard.remote.JSCHClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * 统一启动类
 */
@Component
public class BootStrap {

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
        return new SingleBootStrap(guardContext).launch();
    }

}
