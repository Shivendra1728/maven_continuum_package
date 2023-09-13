package com.continuum.controller;

import java.util.Date;

import org.apache.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import com.continuum.multitenant.util.JwtTokenUtil;

import com.continuum.response.CustomerLoginResponse;

import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.entity.Customer;
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

	@PostMapping("/signupCust")

	public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) throws Exception {

		return customerService.createCustomer(customerDTO);

	}

	@GetMapping("/customer/login")

	public ResponseEntity<CustomerLoginResponse> customerLogin(@RequestParam String email,
			@RequestParam String password) throws Exception {

		CustomerDTO customer = customerService.customerLogin(email, password);
		Customer customer1 = repo.findByEmail(email);
		if (customer != null) {
			String token = jwtTokenUtil.generateToken(email, "tenantOrClientId");
			Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
			String CustId = customer1.getCustomerId();

			CustomerLoginResponse response = new CustomerLoginResponse();
			response.setMessage("Login success!");
			response.setToken(token);
			response.setExpirationDate(expirationDate);
			response.setCustomerId(CustId);
			

			return ResponseEntity.ok(response);
		} else {
			CustomerLoginResponse response = new CustomerLoginResponse();
			response.setMessage("Login Failed!");
			System.err.println("Authentication failed for email: " + email);

			return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(response);
		}

	}

}