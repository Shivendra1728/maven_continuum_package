package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.multitenant.util.JwtTokenUtil;
import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.di.commons.dto.CustomerDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController

public class CustomerController {

	public static String extractedUsername = "";

	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustomerRepository repo;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	public CustomerController(CustomerService customerService, JwtTokenUtil jwtTokenUtil) {

		this.customerService = customerService;

		this.jwtTokenUtil = jwtTokenUtil;

	}

	

	public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) throws Exception {

		return customerService.createCustomer(customerDTO);

	}
	
	@PostMapping("/signupCust")
	public String createCustomerInDB(@RequestBody CustomerDTO customerDTO)  {

		return customerService.createCustomerInDB(customerDTO);

	}

	

}