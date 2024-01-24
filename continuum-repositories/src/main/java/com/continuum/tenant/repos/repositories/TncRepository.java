package com.continuum.tenant.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.TNC;

@Repository
public interface TncRepository extends JpaRepository<TNC, Long> {
}
