package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.EditableConfigService;
import com.continuum.tenant.repos.entity.EditableConfig;

@RestController
@RequestMapping("/editable")
public class EditableConfigController {
	
	@Autowired
	EditableConfigService editableConfigService;

	
	@GetMapping("/search")
	public List<EditableConfig> getConfigurableEdits() {
		
		return editableConfigService.findAll();

	}
}
