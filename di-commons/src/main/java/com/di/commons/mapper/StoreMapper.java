package com.di.commons.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Orders;
import com.continuum.tenant.repos.entity.Store;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.StoreDTO;

@Component
public class StoreMapper {

	@Autowired
	private ModelMapper modelMapper;

	public StoreDTO storeToStoreDTO(Store store) {

		StoreDTO storeDTO = modelMapper.map(store, StoreDTO.class);
		return storeDTO;
	}

	public Store storeDTOToStore(StoreDTO storeDTO) {

		Store store = modelMapper.map(storeDTO, Store.class);
		return store;
	}

}
