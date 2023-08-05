package com.continuum.multitenant.mastertenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

/**
 * @author RK
 */
@Repository
public interface MasterTenantRepository extends JpaRepository<MasterTenant, Integer> {
    MasterTenant findByTenantClientId(Integer clientId);

	MasterTenant findByDbName(String tenentId);
}
