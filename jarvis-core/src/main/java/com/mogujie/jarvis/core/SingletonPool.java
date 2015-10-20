package com.mogujie.jarvis.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SingletonPool {

    private static final Map<Class<?>, Object> POOL = new ConcurrentHashMap<Class<?>, Object>();

    private SingletonPool() {
    }

    public static void put(Object instance) {
        POOL.putIfAbsent(instance.getClass(), instance);
    }

    public static <T> T get(Class<T> clazz) {
        return clazz.cast(POOL.get(clazz));
    }
}
