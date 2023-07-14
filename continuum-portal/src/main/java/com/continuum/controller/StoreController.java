package com.continuum.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.di.commons.helper.*;
import com.continuum.repos.entity.Store;
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
	@GetMapping("/search")
	public Optional<Store> getStoreBysearchCriteria(@RequestParam(required = false) long id) {
		StoreSearchParameters storeSearchParameter = new StoreSearchParameters();
		storeSearchParameter.setId(id);
		return service.getStoreBysearchCriteria(storeSearchParameter);

	}
}
