package com.di.integration.p21.service;

import java.util.Map;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

public interface P21TaxService {

	Map<String, String> getTax(String rmaNo, MasterTenant masterTenantObject) throws Exception;

}
