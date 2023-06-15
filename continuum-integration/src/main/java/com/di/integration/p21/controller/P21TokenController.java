package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21TokenSerivce;

@RestController
@RequestMapping("/P21/token")
public class P21TokenController {
	
	@Autowired
	P21TokenSerivce service;
	@GetMapping("/getToken")
	public String getToken() throws Exception{
		
		return service.getToken();
	}

}
