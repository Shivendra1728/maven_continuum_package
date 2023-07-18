package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.Client;
import com.continuum.repos.entity.ClientConfig;

@Repository
public interface ClientConfigRepository extends JpaRepository<ClientConfig, Long> {

}