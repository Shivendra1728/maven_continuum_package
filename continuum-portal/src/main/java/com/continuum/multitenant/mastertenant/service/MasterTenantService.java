package com.continuum.multitenant.mastertenant.service;

import java.util.List;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

/**
 * @author RK
 */
public interface MasterTenantService {

    MasterTenant findByClientId(Integer clientId);

	MasterTenant findByDbName(String tenentId);

	List<MasterTenant> getAllTenants();
}
