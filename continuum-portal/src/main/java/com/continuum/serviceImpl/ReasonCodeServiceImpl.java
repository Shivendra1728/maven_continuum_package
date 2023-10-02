package com.continuum.serviceImpl;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.ReasonCodeService;
import com.continuum.tenant.repos.entity.Orders;
import com.continuum.tenant.repos.entity.ReasonCode;
import com.continuum.tenant.repos.entity.Store;
import com.continuum.tenant.repos.repositories.ReasonCodeRepository;
import com.continuum.tenant.repos.repositories.StoreRepository;
import com.di.commons.dto.ReasonCodeDTO;
import com.di.commons.mapper.ReasonCodeMapper;

@Service
public class ReasonCodeServiceImpl implements ReasonCodeService {

	@Autowired
	ReasonCodeRepository reasonCodeRepository;

	@Autowired
	ReasonCodeMapper rcMapper;

	@Autowired
	StoreRepository storeRepository;

	@Override
	public List<ReasonCode> searchReasonCodesByStoreId(Long storeId) {
		Store store = storeRepository.findById(storeId).orElse(new Store());
		return reasonCodeRepository.findNestedReasonCodesByStoreId(store);
	}

	@Override
	public List<ReasonCodeDTO> searchReasonFlatCodesByStoreId(Long storeId) {
		Store store = new Store();
		store.setId(storeId);
		List<ReasonCodeDTO> codeDTOs = new ArrayList<>();
		List<ReasonCode> reasonCodes = reasonCodeRepository.findByStore(store);
		for (ReasonCode code : reasonCodes) {
			codeDTOs.add(rcMapper.reasonCodeToReasonCodeDTO(code));
		}
		return codeDTOs;
	}

	@Override
	public String createOrder(ReasonCodeDTO reasonCodeDTO) {
		ReasonCode rc = rcMapper.reasonCodeDTOToReasonCode(reasonCodeDTO);
		reasonCodeRepository.save(rc);
		return "ReasonCode created succssfully";
	}
}