package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.ReturnTypeService;
import com.continuum.tenant.repos.entity.ReturnType;
import com.continuum.tenant.repos.repositories.ReturnTypeRepository;

@Service
public class ReturnTypeServiceImpl implements ReturnTypeService{
	
	@Autowired
	ReturnTypeRepository returnTypeRepository;
	
	@Override
	public List<ReturnType> getAll(){
		return returnTypeRepository.findAll();
		
	}

}
