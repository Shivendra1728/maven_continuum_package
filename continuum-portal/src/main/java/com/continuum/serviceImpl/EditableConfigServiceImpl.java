package com.continuum.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.EditableConfigService;
import com.continuum.tenant.repos.entity.EditableConfig;
import com.continuum.tenant.repos.repositories.EditableConfigRepository;


@Service
public class EditableConfigServiceImpl implements EditableConfigService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	@Autowired
	EditableConfigRepository editableConfigRepository;
	
	
	
	@Override
	public List<EditableConfig> findAll() {
		return editableConfigRepository.findAll();
	}
	

}
