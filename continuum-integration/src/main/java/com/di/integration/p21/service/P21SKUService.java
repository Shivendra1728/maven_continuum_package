package com.di.integration.p21.service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

public interface P21SKUService {

	

	String deleteSKU(String itemId, String rmaNo, MasterTenant masterTenant)throws Exception;

}
