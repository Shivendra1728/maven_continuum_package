package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.CustomerService;

@RestController
public class CustomerController {

	@Autowired	
	CustomerService customerService;
	
}

