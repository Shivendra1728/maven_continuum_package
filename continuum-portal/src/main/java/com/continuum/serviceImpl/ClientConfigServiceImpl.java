package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.Client;
import com.continuum.repos.repositories.ClientRepository;
import com.continuum.service.ClientService;
import com.di.commons.dto.ClientDTO;
import com.di.commons.mapper.ClientMapper;

@Service
public class ClientConfigServiceImpl implements ClientService {

	@Autowired
	ClientRepository clientRepo;

	@Autowired
	ClientMapper clientMapper;

	@Override
	public String createClient(ClientDTO clientDTO) {
		Client client = clientMapper.clientDTOToClient(clientDTO);
		clientRepo.save(client);
		return "Client Added Sucessfully";
	}
}
