package com.continuum.multitenant.mastertenant.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.service.MasterTenantService;
import com.di.commons.helper.DBContextHolder;
import com.di.integration.config.TenantInfoHolder;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

	@Autowired
	private TenantInfoHolder tenantInfoHolder;
	
	@Autowired
	MasterTenantService masterTenantService;

	 @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	            throws Exception {
	        try {
	            String tenantId = request.getHeader("host").split("\\.")[0];
	            MasterTenant masterTenant = masterTenantService.findByDbName(tenantId);

	            
	            DBContextHolder.setCurrentDb(tenantId);

	            
	            tenantInfoHolder.setDomain(masterTenant.getSubdomain());
	            tenantInfoHolder.setDomainUsername(masterTenant.getDomainUsername());
	            tenantInfoHolder.setDomainPassword(masterTenant.getDomainPassword());

	            log.info(tenantId + " : is being used for the request!!!!!!!!!!");
	        } catch (Exception e) {
	            log.error("Error setting the database context", e);
	            // Handle the exception, possibly return an error response
	            return false;
	        }
	        return true;
	    }

	    @Override
	    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
	            throws Exception {
	        try {
	            log.info(DBContextHolder.getCurrentDb() + " : is being released for the request!!!!!!!!!!");
	        } finally {
	            DBContextHolder.clear();
	        }
	    }
}
