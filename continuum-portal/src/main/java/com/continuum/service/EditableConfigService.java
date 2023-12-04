package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.EditableConfig;

public interface EditableConfigService {

	List<EditableConfig> findAll();

}
