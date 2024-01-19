package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.EditableConfigService;
import com.continuum.tenant.repos.entity.EditableConfig;
import com.continuum.tenant.repos.repositories.EditableConfigRepository;

@Service
public class EditableConfigServiceImpl implements EditableConfigService {

	@Autowired
	EditableConfigRepository editableConfigRepository;

	@Override
	public List<EditableConfig> findAll() {
		return editableConfigRepository.findAll();
	}

}
