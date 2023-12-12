package com.di.integration.p21.service;
 
public interface TenantInfoProviderService {
	String getDomainUsername();
    String getDomainPassword();
    String getSubdomain();
    
   public void updateTenantInfo(String subdomain, String domainUsername, String domainPassword);
}