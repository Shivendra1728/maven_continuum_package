package com.continuum.service;

import java.util.List;

import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.StoreSearchParameters;

public interface StoreService {

	String createStore(StoreDTO storeDTO);

	List<StoreDTO> getStoreBysearchCriteria(StoreSearchParameters storeSearchParameter);

}