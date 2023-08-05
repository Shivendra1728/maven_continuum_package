package com.continuum.serviceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	@Override
	public String createClient(ClientDTO clientDTO) {
		Client client = clientMapper.clientDTOToClient(clientDTO);
		clientRepo.save(client);
		return "Client Added Sucessfully";
	}

	@Override
	public String createClientConfig(ClientConfigDTO clientConfigDTO) {
		ClientConfig clientConfig = clientConfigMapper.clientConfigDTOToClientConfig(clientConfigDTO);
		clientConfigRepository.save(clientConfig);
		return "Client Configuration Added Sucessfully";
	}
}