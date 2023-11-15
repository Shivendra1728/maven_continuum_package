package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.WarehouseService;
import com.continuum.tenant.repos.entity.Warehouse;
import com.continuum.tenant.repos.repositories.WarehouseRepository;

@Service
public class WarehouseServiceImpl implements WarehouseService {

	@Autowired
	WarehouseRepository warehouseRepository;
	
	public List<Warehouse> findAll() {
		return warehouseRepository.findAll();
	}
	
	public void createWarehouse(Warehouse warehouse) {
		warehouseRepository.save(warehouse);
	}

}
