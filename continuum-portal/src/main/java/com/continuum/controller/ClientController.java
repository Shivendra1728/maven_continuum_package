package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.clientService;
import com.di.commons.dto.ClientDTO;

@RestController
@RequestMapping("/client")
public class ClientController {
	@Autowired
	clientService clientService;

	@PostMapping("/create/v1")
	public String createOrder(@RequestBody ClientDTO clientDTO) {
		return clientService.createClient(clientDTO);

	}

	
}
