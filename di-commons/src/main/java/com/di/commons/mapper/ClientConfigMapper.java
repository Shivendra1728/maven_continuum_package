package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.ClientConfig;
import com.di.commons.dto.ClientConfigDTO;

@Component
public class ClientConfigMapper {

	@Autowired
	ModelMapper modelMapper;

	public ClientConfigDTO clientConfigToClientConfigDTO(ClientConfig clientConfig) {
		System.out.println("ClientConfigMapper.clientConfigToClientConfigDTO()");
		ClientConfigDTO clientConfigDTO = modelMapper.map(clientConfig, ClientConfigDTO.class);
		return clientConfigDTO;
	}

	public ClientConfig clientConfigDTOToClientConfig(ClientConfigDTO clientConfigDTO) {
		System.out.println("ClientConfigMapper.clientConfigDTOToClientConfig()");
		ClientConfig clientConfig = modelMapper.map(clientConfigDTO, ClientConfig.class);
		return clientConfig;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
	}

}