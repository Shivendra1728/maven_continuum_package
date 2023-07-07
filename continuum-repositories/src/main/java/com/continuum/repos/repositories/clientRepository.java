package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.Client;

@Repository
public interface clientRepository extends JpaRepository<Client, Long> {

	
}
