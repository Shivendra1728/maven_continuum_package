package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.ReturnType;

@Repository
public interface ReturnTypeRepository extends JpaRepository<ReturnType, Long>{

}
