package com.wyj.guard.share.enums;

/**
 * 启动状态：判断是否需要启动
 */
public enum LaunchStatus {

    SHUTDOWN(0),
    UP(1);

    public final Short status;

    LaunchStatus(int status) {
        this.status = (short) status;
    }

    public static LaunchStatus getEnum(Short status) {
        for (LaunchStatus launchStatus : values()) {
            if (launchStatus.status.equals(status)) {
                return launchStatus;
            }
        }
        return null;
    }
}
