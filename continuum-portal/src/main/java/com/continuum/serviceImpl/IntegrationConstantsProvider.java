package com.continuum.serviceImpl;

import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.TenantInfoProviderService;

public class IntegrationConstantsProvider implements TenantInfoProviderService {

	private String subdomain;
	private String domainUsername;
	private String domainPassword;

	@Override
	public String getSubdomain() {
		return subdomain;
	}

	@Override
	public String getDomainUsername() {
		return domainUsername;
	}

	@Override
	public String getDomainPassword() {
		return domainPassword;
	}

	public void updateTenantInfo(String subdomain, String domainUsername, String domainPassword) {
		this.subdomain = subdomain;
		this.domainUsername = domainUsername;
		this.domainPassword = domainPassword;
	}
}
