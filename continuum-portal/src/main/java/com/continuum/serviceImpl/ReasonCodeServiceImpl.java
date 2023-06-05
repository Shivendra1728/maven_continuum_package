package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.ReasonCode;
import com.continuum.repos.entity.Store;
import com.continuum.repos.repositories.ReasonCodeRepository;
import com.continuum.repos.repositories.StoreRepository;
import com.continuum.service.ReasonCodeService;
import com.di.commons.dto.ReasonCodeDTO;
import com.di.commons.mapper.ReasonCodeMapper;
@Service
public class ReasonCodeServiceImpl implements ReasonCodeService{

	@Autowired
	ReasonCodeRepository repository;
	@Autowired
	ReasonCodeMapper rcMapper;
	@Autowired
	StoreRepository storeRepository;
	
	/*
	 * @Override public List<ReasonCodeDTO> searchReasonCodesByStoreId(long storeId)
	 * {
	 * 
	 * List<ReasonCodeDTO> rcDTOs=new ArrayList<>();
	 * repository.findAll().forEach(reasoncode->{
	 * rcDTOs.add(rcMapper.reasonCodeToReasonCodeDTO(reasoncode)); }); return
	 * rcDTOs; }
	 */

	@Override
	public List<ReasonCode> searchReasonCodesByStoreId(Long storeId ) {
	Store store=	storeRepository.findById(storeId).orElse(new Store());
		return  repository.findNestedReasonCodesByStoreId(store);
	}

	@Override
	public List<ReasonCodeDTO> searchReasonFlatCodesByStoreId(Long storeId) {
		Store store=	new Store();
		store.setId(storeId);
		List<ReasonCodeDTO> codeDTOs= new ArrayList<>();
		List<ReasonCode> reasonCodes=repository.findByStore(store);
		for( ReasonCode code: reasonCodes ) {
			codeDTOs.add(rcMapper.reasonCodeToReasonCodeDTO(code));
		}
	codeDTOs.forEach(code->{System.out.println(code);});
		return codeDTOs;
	}
	}
