package com.wyj.guard.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureUtils {

    public static Logger logger = LoggerFactory.getLogger(CompletableFutureUtils.class);

    public static <T> boolean allOf(List<CompletableFuture<T>> list) {
        try {
            CompletableFuture.allOf(list.toArray(new CompletableFuture[list.size()])).get();
        } catch (Exception e) {
            logger.error("执行任务失败!", e);
            return false;
        }
        return true;
    }

}
