package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Client;
import com.di.commons.dto.ClientDTO;

@Component
public class ClientMapper {

	@Autowired
	ModelMapper modelMapper;

	public ClientDTO clientToClientDTO(Client client) {

		ClientDTO clientDTO = modelMapper.map(client, ClientDTO.class);
		return clientDTO;
	}

	public Client clientDTOToClient(ClientDTO clientDTO) {
		Client client = modelMapper.map(clientDTO, Client.class);
		return client;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());

	}
}
