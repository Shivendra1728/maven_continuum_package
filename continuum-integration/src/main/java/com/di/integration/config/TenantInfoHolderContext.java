package com.di.integration.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@Scope(value = "Singleton")
public class TenantInfoHolderContext {
    private static final ThreadLocal<TenantInfoHolder> threadLocal = new ThreadLocal<>();

    public static TenantInfoHolder getCurrentTenantInfo() {
        return threadLocal.get();
    }

    public static void setCurrentTenantInfo(TenantInfoHolder tenantInfoHolder) {
        threadLocal.set(tenantInfoHolder);
    }

    public static void clear() {
        threadLocal.remove();
    }
}

