package com.continuum.service;

import java.util.List;
import java.util.Optional;

import com.continuum.repos.entity.Store;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.StoreSearchParameters;

public interface StoreService {

	String createStore(StoreDTO storeDTO);

	Optional<Store> getStoreBysearchCriteria(StoreSearchParameters storeSearchParameter);

}