package com.continuum.multitenant.mastertenant.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;

/**
 * @author RK
 */
@Service
public class MasterTenantServiceImpl implements MasterTenantService{

    private static final Logger LOG = LoggerFactory.getLogger(MasterTenantServiceImpl.class);

    @Autowired
    MasterTenantRepository masterTenantRepository;


    @Override
    public MasterTenant findByClientId(Integer clientId) {
        LOG.info("findByClientId() method call...");
        return masterTenantRepository.findByTenantClientId(clientId);
    }


	@Override
	public MasterTenant findByDbName(String tenentId) {
		 LOG.info("findByDbName() method call...");
	        return masterTenantRepository.findByDbName(tenentId);
	    }


	@Override
	public List<MasterTenant> getAllTenants() {
		// TODO Auto-generated method stub
		  return masterTenantRepository.findAll();
	}
	
}
