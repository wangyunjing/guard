package com.wyj.guard.info.loader;

import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.DBInstanceConfig;
import com.wyj.guard.info.config.InstanceConfig;
import com.wyj.guard.share.Pair;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 基于数据库的配置加载器
 */
public class DBInstanceConfigLoader extends InstanceConfigLoader {

    private JdbcTemplate jdbcTemplate;

    public DBInstanceConfigLoader(AppConfigLoader appConfigLoader, JdbcTemplate jdbcTemplate) {
        super(appConfigLoader);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public InstanceConfig[] load(String applicationName) {

        // 查询是否存在该应用
        ApplicationConfig applicationConfig = findApplicationConfig(applicationName);
        if (applicationConfig == null) {
            return new InstanceConfig[0];
        }

        String sql = "SELECT instance_id FROM instance WHERE application_id = ?";

        List<String> ids = jdbcTemplate.query(sql, new Object[]{applicationConfig.getApplicationId()}, (resultSet, rowNum) -> {
            return resultSet.getString(1);
        });

        List<InstanceConfig> configList = new ArrayList<>();

        ids.stream().forEach((id) -> {
            DBInstanceConfig instanceConfig = new DBInstanceConfig(jdbcTemplate, applicationConfig, id);
            configList.add(instanceConfig);
        });

        return configList.toArray(new InstanceConfig[configList.size()]);
    }

    @Override
    public InstanceConfig load(String applicationName, String instanceId) {

        // 查询是否存在该应用
        ApplicationConfig applicationConfig = findApplicationConfig(applicationName);
        if (applicationConfig == null) {
            return null;
        }

        String sql = "SELECT instance_id FROM instance WHERE application_id = ?"
                + " AND instance_id = ?";

        String id = jdbcTemplate.query(sql, new Object[]{applicationConfig.getApplicationId(), instanceId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });

        if (id == null) {
            return null;
        }

        return new DBInstanceConfig(jdbcTemplate, applicationConfig, id);
    }

    @Override
    public InstanceConfig[] load() {
        ApplicationConfig[] applicationConfigs = loadApplicationConfigs();

        String sql = "SELECT instance_id, application_id FROM instance";

        List<Pair<String, Integer>> ids = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            String instanceId = resultSet.getString(1);
            Integer applicationId = resultSet.getInt(2);
            return Pair.newPair(instanceId, applicationId);
        });

        List<InstanceConfig> configList = new ArrayList<>();

        ids.stream().forEach((id) -> {

            ApplicationConfig applicationConfig = findApplicationConfig(applicationConfigs, id.getSecond());

            if (applicationConfig != null) {
                DBInstanceConfig instanceConfig = new DBInstanceConfig(jdbcTemplate, applicationConfig, id.getFirst());
                configList.add(instanceConfig);
            }

        });

        return configList.toArray(new InstanceConfig[configList.size()]);
    }

    private ApplicationConfig[] loadApplicationConfigs() {
        return appConfigLoader.load();
    }

    // 根据应用名查询对应的应用
    private ApplicationConfig findApplicationConfig(String applicationName) {
        return appConfigLoader.load(applicationName);
    }

    // 根据应用ID查询对应的应用
    private ApplicationConfig findApplicationConfig(ApplicationConfig[] applicationConfigs, Integer applicationId) {
        return Arrays.stream(applicationConfigs).filter((config) ->
                applicationId.equals(config.getApplicationId())
        ).findFirst().orElse(null);
    }

}
