package com.wyj.guard.db;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("my.datasource")
public class DataSourceProperties {

    private String url = "jdbc:mysql://localhost:3306/process_scheduling?useUnicode=true&characterEncoding=UTF8&useSSL=false";

    private String username = "root";

    private String password = "root";

    private Integer initSize = 10;

    private Integer minIdle = 5;

    private Integer maxActive = 100;

    private Integer maxWait = 30000;

    private Integer maxCreateTaskCount = 3;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getInitSize() {
        return initSize;
    }

    public void setInitSize(Integer initSize) {
        this.initSize = initSize;
    }

    public Integer getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(Integer minIdle) {
        this.minIdle = minIdle;
    }

    public Integer getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }

    public Integer getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(Integer maxWait) {
        this.maxWait = maxWait;
    }

    public Integer getMaxCreateTaskCount() {
        return maxCreateTaskCount;
    }

    public void setMaxCreateTaskCount(Integer maxCreateTaskCount) {
        this.maxCreateTaskCount = maxCreateTaskCount;
    }

}
