package com.di.commons.helper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RK
 * The context holder implementation is a container that stores the current context as a ThreadLocal reference.
 */

@Slf4j
public class DBContextHolder {
	
	

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentDb(String dbType) {
    	
        contextHolder.set(dbType);
    }

    public static String getCurrentDb() {
    	
    	//log.info(contextHolder.get());
        return contextHolder.get();
        
    }

    public static void clear() {
        contextHolder.remove();
    }
}
