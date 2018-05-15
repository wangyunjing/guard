package com.wyj.guard.utils;

import java.util.concurrent.ExecutorService;

public class ThreadPoolUtils {

    /**
     * 关闭线程池（所有的任务全部执行完成，包括正在执行的任务和等待的任务）
     */
    public static void shutdown(ExecutorService threadPool) {
        if (threadPool == null || threadPool.isShutdown()) {
            return;
        }
        threadPool.shutdown();
        while (true) {
            boolean terminated = threadPool.isTerminated();
            if (terminated) {
                break;
            }
        }
    }


    public static boolean isAvailable(ExecutorService threadPool) {
        if (threadPool == null || threadPool.isShutdown()) {
            return false;
        }
        return true;
    }
}
