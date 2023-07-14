package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.Orders;
import com.continuum.repos.entity.Store;
import com.continuum.repos.repositories.StoreRepository;
import com.continuum.service.StoreService;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.StoreSearchParameters;
import com.di.commons.mapper.StoreMapper;

@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	StoreRepository repository;

	@Autowired
	StoreMapper storeMapper;

	@Override
	public String createStore(StoreDTO storeDTO) {
		Store store = storeMapper.storeDTOToStore(storeDTO);
		repository.save(store);
		return "Store created Sucessfully";
	}

	@Override
	public Optional<Store> getStoreBysearchCriteria(StoreSearchParameters storeSearchParameter) {

		if (storeSearchParameter.getId() != null) {

			return repository.findById(storeSearchParameter.getId());
		}
		return Optional.empty();
	}
}