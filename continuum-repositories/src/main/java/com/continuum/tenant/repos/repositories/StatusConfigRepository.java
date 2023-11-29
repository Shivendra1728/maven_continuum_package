package com.continuum.tenant.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.continuum.tenant.repos.entity.StatusConfig;

public interface StatusConfigRepository extends JpaRepository<StatusConfig, Long> {
	List<StatusConfig> findBystatuslabl(String statusType);
}
