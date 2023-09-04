package com.continuum.tenant.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.tenant.repos.entity.Role;

@Repository
public interface RolesRepository extends JpaRepository<Role, Long> {

}
