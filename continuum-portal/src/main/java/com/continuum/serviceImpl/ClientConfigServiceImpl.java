package com.continuum.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.service.ClientService;
import com.continuum.tenant.repos.entity.Client;
import com.continuum.tenant.repos.entity.ClientConfig;
import com.continuum.tenant.repos.repositories.ClientConfigRepository;
import com.continuum.tenant.repos.repositories.ClientRepository;
import com.di.commons.dto.ClientConfigDTO;
import com.di.commons.dto.ClientDTO;
import com.di.commons.mapper.ClientConfigMapper;
import com.di.commons.mapper.ClientMapper;

@Service
public class ClientConfigServiceImpl implements ClientService {

	@Autowired
	ClientRepository clientRepo;
	@Autowired
	ClientConfigRepository clientConfigRepository;
	@Autowired
	ClientMapper clientMapper;
	@Autowired
	ClientConfigMapper clientConfigMapper;
	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Override
	public String createClient(ClientDTO clientDTO) {
		Client client = clientMapper.clientDTOToClient(clientDTO);
		clientRepo.save(client);
		return "Client Added Sucessfully";
	}

	@Override
	public String createClientConfig(ClientConfigDTO clientConfigDTO) {
		ClientConfig clientConfig = clientConfigMapper.clientConfigDTOToClientConfig(clientConfigDTO);

		MasterTenant masterTenantData = clientConfigDTO.getMasterTenant();

		MasterTenant masterTenant = new MasterTenant();
		masterTenant.setTenantClientId(masterTenantData.getTenantClientId());
		masterTenant.setDbName(masterTenantData.getDbName());
		masterTenant.setDriverClass(masterTenantData.getDriverClass());
		masterTenant.setPassword(masterTenantData.getPassword());
		masterTenant.setStatus(masterTenantData.getStatus());
		masterTenant.setUrl(masterTenantData.getUrl());
		masterTenant.setUserName(masterTenantData.getUserName());

		masterTenantRepository.save(masterTenant);
		clientConfigRepository.save(clientConfig);

		return "Client Configuration Added Sucessfully";
	}
}