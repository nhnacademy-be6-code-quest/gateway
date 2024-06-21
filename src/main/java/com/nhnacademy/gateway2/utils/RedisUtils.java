package com.nhnacademy.gateway2.utils;

public class RedisUtils {
    private RedisUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTokenPrefix() {
        return "Token:";
    }
}
