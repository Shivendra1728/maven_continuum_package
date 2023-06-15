package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.StoreService;
import com.di.commons.dto.StoreDTO;

@RestController
@RequestMapping("/store")
public class StoreController {
	
	@Autowired
	StoreService service;

	@PostMapping("/create")
	public void createStore(@RequestBody StoreDTO storeDTO) {
		
		service.createStore(storeDTO);
	}
}
