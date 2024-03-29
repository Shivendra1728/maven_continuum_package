package com.continuum.service;

import com.di.commons.dto.ClientConfigDTO;
import com.di.commons.dto.ClientDTO;

public interface ClientService {
	String createClient(ClientDTO clientDTO);

	String createClientConfig(ClientConfigDTO clientConfigDTO);
}