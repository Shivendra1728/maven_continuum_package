package com.continuum.repos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.ReturnOrder;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long>{

}
