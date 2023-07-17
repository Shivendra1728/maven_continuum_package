package com.continuum.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.di.commons.helper.*;
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
	public List<StoreDTO> getStoreBysearchCriteria(@RequestParam(required = false) String storeName) {
		StoreSearchParameters storeSearchParameter = new StoreSearchParameters();
		storeSearchParameter.setStroreName(storeName);
		return service.getStoreBysearchCriteria(storeSearchParameter);

	}
}
