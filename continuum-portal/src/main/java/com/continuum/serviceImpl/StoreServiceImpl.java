package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.continuum.repos.repositories.StoreRepository;
import com.continuum.service.StoreService;
import com.di.commons.dto.StoreDTO;
import com.di.commons.mapper.StoreMapper;
@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	StoreRepository repository;
	@Autowired
	StoreMapper storeMapper;
	
	@Override
	public void createStore(StoreDTO storeDTO) {
		repository.save(storeMapper.storeDTOToStore(storeDTO));
	}

}
