package com.wyj.guard.utils;

import java.util.Date;

public class DateTimeUtils {

    public static long getCurrentTime() {
        return new Date().getTime();
    }


    public static long delayTime(long futureTime) {
        long currentTime = getCurrentTime();
        long delay = futureTime - currentTime;
        delay = delay < 0 ? 0 : delay;
        return delay;
    }

}
