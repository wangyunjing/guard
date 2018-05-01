package com.wyj.guard.utils;

/**
 * 占位符为$
 */
public class StringPlaceholderResolver {


    public static String resolvePlaceholder(String placeholder, String... args) {
        for (String s : args) {

            placeholder = placeholder.replaceFirst("\\$", s);
        }
        return placeholder;
    }
}
