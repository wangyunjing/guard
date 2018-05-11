package com.wyj.guard.web;

public interface GuardManagementEndpoint {

    /**
     * 刷新配置
     */
    void refresh(Integer applicationId, String instanceId);

    /**
     * 关闭所有的实例
     */
    void close();

    /**
     * 开启所有的实例
     */
    void open();

}
