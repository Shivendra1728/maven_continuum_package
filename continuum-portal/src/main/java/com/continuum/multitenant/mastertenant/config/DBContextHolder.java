package com.continuum.multitenant.mastertenant.config;

/**
 * @author RK
 * The context holder implementation is a container that stores the current context as a ThreadLocal reference.
 */
public class DBContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentDb(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getCurrentDb() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
