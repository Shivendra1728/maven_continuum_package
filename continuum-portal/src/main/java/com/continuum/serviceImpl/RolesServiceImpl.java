package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.RolesService;
import com.continuum.tenant.repos.entity.Role;
import com.continuum.tenant.repos.repositories.RolesRepository;

@Service
public class RolesServiceImpl implements RolesService {
	@Autowired
	RolesRepository rolesRepository;

	@Override
	public List<Role> getRolesWithPermissions() {
		return rolesRepository.findAll();
	}
}
