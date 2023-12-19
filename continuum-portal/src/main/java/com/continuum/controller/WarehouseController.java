package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.WarehouseService;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.entity.Warehouse;

@RestController
public class WarehouseController {

	@Autowired
	WarehouseService warehouseService;
	
	@PostMapping("/createWarehouse")
	public String createWarehouse(@RequestBody Warehouse warehouse) throws Exception{
		return warehouseService.createWarehouse(warehouse);
	}
	
	@GetMapping("/getWarehouse")
	public List<Warehouse> getWarehouse(){
		return warehouseService.findAll();
	}
	
}
