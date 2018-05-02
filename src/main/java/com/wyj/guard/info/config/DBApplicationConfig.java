package com.wyj.guard.info.config;

import com.wyj.guard.share.enums.LaunchStatus;
import com.wyj.guard.utils.StringPlaceholderResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 基于数据库的配置
 */
public class DBApplicationConfig extends AbstractApplicationConfig {

    public final Logger logger = LoggerFactory.getLogger(DBApplicationConfig.class);

    private final String DEFAULT_QUERY_PLACEHOLDER_SQL;
    private final String DEFAULT_UPDATE_PLACEHOLDER_SQL;

    private JdbcTemplate jdbcTemplate;

    public DBApplicationConfig(JdbcTemplate jdbcTemplate, int applicationId) {
        super(applicationId);
        this.jdbcTemplate = jdbcTemplate;

        this.DEFAULT_QUERY_PLACEHOLDER_SQL = "SELECT $ FROM application WHERE id = ?";
        this.DEFAULT_UPDATE_PLACEHOLDER_SQL = "UPDATE application SET $=? WHERE id = ?";
    }

    @Override
    public Integer getApplicationId() {
        return applicationId;
    }

    @Override
    public String getApplicationName() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "application_name");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            throw new RuntimeException("没有设置应用名称");
        });
    }

    @Override
    public Integer getDefaultPort() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "port");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public String getDefaultHealthUrl() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "health_url");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
    }

    @Override
    public String getDefaultStartCommand() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "start_command");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
    }

    @Override
    public Integer getStartInstanceNum() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "start_instance_num");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public String getDefaultUsername() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "username");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
    }

    @Override
    public String getDefaultPassword() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "password");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        });
    }

    @Override
    public void setDefaultHealthUrl(String healthUrl) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "health_url");
        jdbcTemplate.update(sql, healthUrl, applicationId);
    }

    @Override
    public void setDefaultStartCommand(String startCommand) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "start_command");
        jdbcTemplate.update(sql, startCommand, applicationId);
    }

    @Override
    public void setStartInstanceNum(Integer startInstanceNum) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "start_instance_num");
        jdbcTemplate.update(sql, startInstanceNum, applicationId);
    }

    @Override
    public void setDefaultUsername(String username) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "username");
        jdbcTemplate.update(sql, username, applicationId);
    }

    @Override
    public void setDefaultPassword(String password) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "password");
        jdbcTemplate.update(sql, password, applicationId);
    }

    @Override
    public void setDefendInstanceDuration(Integer duration) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "defend_instance_duration");
        jdbcTemplate.update(sql, duration, applicationId);
    }

    @Override
    public Integer getDefendInstanceDuration() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "defend_instance_duration");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public void setDefaultHeartbeatRate(Integer rate) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "heartbeat_rate");
        jdbcTemplate.update(sql, rate, applicationId);
    }

    @Override
    public void setDefaultInitializeInstanceDuration(Integer duration) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "initialize_instance_duration");
        jdbcTemplate.update(sql, duration, applicationId);
    }

    @Override
    public void setDefaultSelfProtectedDuration(Integer duration) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "self_protected_duration");
        jdbcTemplate.update(sql, duration, applicationId);
    }

    @Override
    public Integer getDefaultHeartbeatRate() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "heartbeat_rate");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public Integer getDefaultInitializeInstanceDuration() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "initialize_instance_duration");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public Integer getDefaultSelfProtectedDuration() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "self_protected_duration");

        return jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return null;
        });
    }

    @Override
    public void setStatus(LaunchStatus launchStatus) {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_UPDATE_PLACEHOLDER_SQL, "status");
        jdbcTemplate.update(sql, launchStatus.status, applicationId);
    }

    @Override
    public LaunchStatus getStatus() {
        String sql = StringPlaceholderResolver.resolvePlaceholder(DEFAULT_QUERY_PLACEHOLDER_SQL, "status");

        Short status = jdbcTemplate.query(sql, new Object[]{applicationId}, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getShort(1);
            }
            return null;
        });
        return LaunchStatus.getEnum(status);
    }
}
