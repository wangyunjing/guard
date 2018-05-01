package com.wyj.guard.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("guard")
public class GuardProperties {

    // 是否集群模式
    private boolean whetherCluster = true;

    // 从Http连接池中获取连接的超时时间
    private Integer connectionRequestTimeout = 5000;

    // 建立TCP的超时时间（即三次握手）
    private Integer connectTimeout = 20000;

    // 读取数据的超时时间
    private Integer readTimeout = 5000;

    public boolean isWhetherCluster() {
        return whetherCluster;
    }

    public void setWhetherCluster(boolean whetherCluster) {
        this.whetherCluster = whetherCluster;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
}
