package com.di.integration.p21.service;

import java.util.Map;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

public interface P21ShipInfoService {

	Map<String, String> getShipInfo(String orderNo, MasterTenant masterTenantObject) throws Exception;

}
