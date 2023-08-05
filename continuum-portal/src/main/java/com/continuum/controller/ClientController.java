package com.continuum.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ClientService;
import com.continuum.tenant.repos.entity.ClientConfig;
import com.di.commons.dto.ClientConfigDTO;
import com.di.commons.dto.ClientDTO;

@RestController
@RequestMapping("/client")
public class ClientController {
	@Autowired
	ClientService clientService;
	@PostMapping("/create/v1")
	public String createOrder(@RequestBody ClientDTO clientDTO) {
		return clientService.createClient(clientDTO);
	}
	@PostMapping("/configure/create/v1")
	public String createConfigOrder(@RequestBody ClientConfigDTO clientConfigDTO) {
		return clientService.createClientConfig(clientConfigDTO);
	}
}
