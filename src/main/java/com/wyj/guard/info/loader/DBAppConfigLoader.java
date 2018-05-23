package com.wyj.guard.info.loader;

import com.wyj.guard.info.config.ApplicationConfig;
import com.wyj.guard.info.config.DBApplicationConfig;
import com.wyj.guard.web.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于数据库的配置加载器
 */
public class DBAppConfigLoader extends AppConfigLoader {

    private Logger logger = LoggerFactory.getLogger(DBAppConfigLoader.class);

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

    @Override
    public ApplicationConfig addApplication(Application application) {
        String sql = "INSERT INTO application(application_name, port, health_url, start_instance_num," +
                "start_command, username, password, heartbeat_rate, initialize_instance_duration," +
                "self_protected_duration, status, defend_instance_duration) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        List<Object> list = new ArrayList<>();
        list.add(application.getApplicationName());
        list.add(application.getPort());
        list.add(application.getHealthUrl());
        list.add(application.getStartInstanceNum());
        list.add(application.getStartCommand());
        list.add(application.getUsername());
        list.add(application.getPassword());
        list.add(application.getHeartbeatRate());
        list.add(application.getInitializeInstanceDuration());
        list.add(application.getSelfProtectedDuration());
        list.add(application.getStatus());
        list.add(application.getDefendInstanceDuration());
        jdbcTemplate.update(sql, list.toArray());
        return load(application.getApplicationName());
    }

    @Override
    public boolean removeApplication(Integer applicationId) {
        try {
            jdbcTemplate.update("DELETE FROM application WHERE id = ?", applicationId);
        } catch (Exception e) {
            logger.error("移除应用{}失败！", applicationId, e);
            return false;
        }
        return false;
    }

    @Override
    public boolean updateApplication(Application application) {
        String sql = "UPDATE application SET port=?,health_url=?,start_instance_num=?," +
                "start_command=?,username=?,password=?,heartbeat_rate=?,initialize_instance_duration=?," +
                "self_protected_duration=?,defend_instance_duration=? WHERE id=?";
        List<Object> list = new ArrayList<>();
        list.add(application.getPort());
        list.add(application.getHealthUrl());
        list.add(application.getStartInstanceNum());
        list.add(application.getStartCommand());
        list.add(application.getUsername());
        list.add(application.getPassword());
        list.add(application.getHeartbeatRate());
        list.add(application.getInitializeInstanceDuration());
        list.add(application.getSelfProtectedDuration());
        list.add(application.getDefendInstanceDuration());
        list.add(application.getApplicationId());
        jdbcTemplate.update(sql, list.toArray());
        return true;
    }
}
