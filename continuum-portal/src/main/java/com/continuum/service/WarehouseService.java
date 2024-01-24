package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.Warehouse;

public interface WarehouseService {
	List<Warehouse> findAll();

	public String createWarehouse(Warehouse warehouse);
}
