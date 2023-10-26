package com.continuum.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.ClientDTO;


@RestController
@RequestMapping("/hello")
public class HelloWorld {
	
	@GetMapping("/world")
	public String createOrder() {
		return "Hello World test!!!!!!!";
	}

}
