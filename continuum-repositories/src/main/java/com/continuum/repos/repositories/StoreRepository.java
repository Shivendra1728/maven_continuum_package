package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long>{

}
