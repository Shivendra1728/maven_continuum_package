package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.RolesService;
import com.continuum.tenant.repos.entity.Role;

@RestController
public class RolesController {
	
	@Autowired
	RolesService roleService;
	
	@GetMapping("/roles")
    public List<Role> getRolesWithPermissions() {
        return roleService.getRolesWithPermissions();
    }
}

