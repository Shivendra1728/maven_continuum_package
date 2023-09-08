package com.continuum.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.CustomerService;
import com.di.commons.dto.CustomerDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class CustomerController {

	@Autowired	
	CustomerService customerService;
	
	@PostMapping("/signupCust")
	    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) throws Exception {
	        return customerService.createCustomer(customerDTO);
	        
	    }
	
}

