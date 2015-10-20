package com.mogujie.jarvis.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonPool {

    private static final Map<Class<?>, Object> POOL = new ConcurrentHashMap<Class<?>, Object>();

    private SingletonPool() {
    }

    public static void put(Object instance) {
        POOL.putIfAbsent(instance.getClass(), instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> clazz) {
        return (T) POOL.get(clazz);
    }
}
