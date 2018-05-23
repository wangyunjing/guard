package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.utils.StringPlaceholderResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基于数据库的配置
 */
public class DBInstanceConfig extends AbstractInstanceConfig {

    public final Logger logger = LoggerFactory.getLogger(DBInstanceConfig.class);

    private final String DEFAULT_QUERY_PLACEHOLDER_SQL;
    private final String DEFAULT_UPDATE_PLACEHOLDER_SQL;

    private JdbcTemplate jdbcTemplate;

    private Object[] args;

    public DBInstanceConfig(JdbcTemplate jdbcTemplate,
                            ApplicationConfig applicationConfig,
                            String instanceId) {
        super(applicationConfig, instanceId);
        this.jdbcTemplate = jdbcTemplate;
        this.DEFAULT_QUERY_PLACEHOLDER_SQL = "SELECT $ FROM instance where instance_id = ?";
        this.DEFAULT_UPDATE_PLACEHOLDER_SQL = "UPDATE instance SET $=? WHERE instance_id=?";
        args = new Object[]{instanceId};
    }

    @Override
    public String getIp() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "ip");

        return jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            // TODO: 2018/1/26
            throw new RuntimeException("没有设置实例IP");
        });
    }

    @Override
    protected Integer doGetPort() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "port");

        Integer port = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
        return port;
    }

    @Override
    protected String doGetStartCommand() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "start_command");
        String startCommand = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
        return startCommand;
    }

    @Override
    protected String doGetHealthUrl() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "health_url");

        String healthUrl = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
        return healthUrl;
    }

    @Override
    protected Integer doGetWeight() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "weight");

        Integer weight = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
        return weight;
    }

    @Override
    protected String doGetUsername() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "username");

        String username = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
        return username;
    }

    @Override
    protected String doGetPassword() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "password");

        String password = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
        return password;
    }

    @Override
    protected Integer doGetHeartbeatRate() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "heartbeat_rate");

        Integer heartbeatRate = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
        return heartbeatRate;
    }

    @Override
    protected Integer doGetInitializeInstanceDuration() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "initialize_instance_duration");

        Integer initializeInstanceDuration = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
        return initializeInstanceDuration;
    }

    @Override
    protected Integer doGetSelfProtectedDuration() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "self_protected_duration");

        Integer selfProtectedDuration = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
        return selfProtectedDuration;
    }

    @Override
    protected LaunchStatus doGetStatus() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "status");
        Short status = jdbcTemplate.query(sql, args, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getShort(1);
            }
            return null;
        });
        return LaunchStatus.getEnum(status);
    }

    @Override
    public void setHealthUrl(String healthUrl) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "health_url");
        jdbcTemplate.update(sql, healthUrl, instanceId);
    }

    @Override
    public void setWeight(Integer weight) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "weight");
        jdbcTemplate.update(sql, weight, instanceId);
    }

    @Override
    public void setStartCommand(String startCommand) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "start_command");
        jdbcTemplate.update(sql, startCommand, instanceId);
    }

    @Override
    public void setUsername(String username) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "username");
        jdbcTemplate.update(sql, username, instanceId);
    }

    @Override
    public void setPassword(String password) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "password");
        jdbcTemplate.update(sql, password, instanceId);
    }

    @Override
    public void setHeartbeatRate(Integer heartbeatRate) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "heartbeat_rate");
        jdbcTemplate.update(sql, heartbeatRate, instanceId);
    }

    @Override
    public void setInitializeInstanceDuration(Integer duration) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "initialize_instance_duration");
        jdbcTemplate.update(sql, duration, instanceId);
    }

    @Override
    public void setSelfProtectedDuration(Integer duration) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "self_protected_duration");
        jdbcTemplate.update(sql, duration, instanceId);
    }

    @Override
    public void setStatus(LaunchStatus launchStatus) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "status");
        jdbcTemplate.update(sql, launchStatus.status, instanceId);
    }

}
