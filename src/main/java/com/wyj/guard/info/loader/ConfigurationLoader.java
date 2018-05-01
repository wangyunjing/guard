package com.wyj.guard.info.loader;

/**
 * 配置加载
 * @param <T>
 */
public interface ConfigurationLoader<T> {

    T[] load();
}
