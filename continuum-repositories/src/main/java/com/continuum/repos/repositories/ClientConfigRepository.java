package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.ClientConfig;

@Repository
public interface ClientConfigRepository extends JpaRepository<ClientConfig, Long>, JpaSpecificationExecutor<ClientConfig>{


	ClientConfig findByErpCompanyId(String companyId);

}