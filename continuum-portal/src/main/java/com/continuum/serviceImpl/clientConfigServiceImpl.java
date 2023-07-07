package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.Client;
import com.continuum.repos.repositories.clientRepository;
import com.continuum.service.clientService;
import com.di.commons.dto.ClientDTO;
import com.di.commons.mapper.ClientMapper;

@Service
public class clientConfigServiceImpl implements clientService {

	@Autowired
	clientRepository clientRepo;

	@Autowired
	ClientMapper clientMapper;

	@Override
	public String createClient(ClientDTO clientDTO) {
		Client client = clientMapper.clientDTOToClient(clientDTO);
		clientRepo.save(client);
		return "Client Added Sucessfully";
	}
}
