package com.di.integration.p21.service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

public interface P21TokenSerivce {

		String getToken(MasterTenant masterTenant) throws Exception;

}
