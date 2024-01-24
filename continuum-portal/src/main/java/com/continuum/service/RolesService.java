package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.Role;

public interface RolesService {

	List<Role> getRolesWithPermissions();

}
