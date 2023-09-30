package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.service.StoreService;
import com.continuum.tenant.repos.entity.Orders;
import com.continuum.tenant.repos.entity.Store;
import com.continuum.tenant.repos.repositories.StoreRepository;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.StoreSearchParameters;
import com.di.commons.mapper.StoreMapper;

@Service
public class StoreServiceImpl implements StoreService {

	@Autowired
	StoreRepository StoreRepository;

	@Autowired
	StoreMapper storeMapper;

	@Override
	public String createStore(StoreDTO storeDTO) {
		Store store = storeMapper.storeDTOToStore(storeDTO);
		StoreRepository.save(store);
		return "Store created Sucessfully";
	}

	@Override
	public List<StoreDTO> getStoreBysearchCriteria(StoreSearchParameters storeSearchParameter) {

		Specification<Store> spec = Specification.where(null);

		if (isNotNullAndNotEmpty(storeSearchParameter.getStroreName())) {
			Specification<Store> Strname = (root, query, builder) -> builder.equal(root.get("storeName"),
					storeSearchParameter.getStroreName());
			spec = spec.and(Strname);
		}

		List<Store> storeList = StoreRepository.findAll(spec);
		List<StoreDTO> storeDTOList = new ArrayList<>();
		storeList.forEach(store -> {
			storeDTOList.add(storeMapper.storeToStoreDTO(store));
		});
		return storeDTOList;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}
}