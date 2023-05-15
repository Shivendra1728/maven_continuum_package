package com.continuum.repos.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.repos.entity.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository  extends JpaRepository<PurchaseOrder, Long>, JpaSpecificationExecutor<PurchaseOrder>{


}
