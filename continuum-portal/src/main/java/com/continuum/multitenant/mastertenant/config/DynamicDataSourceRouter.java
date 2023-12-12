package com.continuum.multitenant.mastertenant.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.di.commons.helper.DBContextHolder;

public class DynamicDataSourceRouter extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DBContextHolder.getCurrentDb();
    }
}
