package com.wyj.guard.info.loader;

import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.DBApplicationConfig;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于数据库的配置加载器
 */
public class DBAppConfigLoader extends AppConfigLoader {

    private JdbcTemplate jdbcTemplate;

    public DBAppConfigLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ApplicationConfig[] load() {

        String sql = "SELECT id FROM application";

        List<Integer> ids = jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            return resultSet.getInt(1);
        });

        List<ApplicationConfig> configList = new ArrayList<>();

        ids.stream().forEach((id) -> {
            DBApplicationConfig dbApplicationConfig = new DBApplicationConfig(jdbcTemplate, id);
            configList.add(dbApplicationConfig);
        });

        return configList.toArray(new ApplicationConfig[configList.size()]);
    }

    @Override
    public ApplicationConfig load(String applicationName) {

        String sql = "SELECT id FROM application WHERE application_name = ?";

        Integer id = jdbcTemplate.query(sql, new Object[]{applicationName}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });

        if (id == null) {
            return null;
        }

        return new DBApplicationConfig(jdbcTemplate, id);
    }

    @Override
    public ApplicationConfig load(Integer applicationId) {
        String sql = "SELECT id FROM application WHERE id = ?";

        Integer id = jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });

        if (id == null) {
            return null;
        }

        return new DBApplicationConfig(jdbcTemplate, id);
    }
}
