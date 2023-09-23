package com.continuum.tenant.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.continuum.tenant.repos.entity.ReturnOrderItem;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {

	List<ReturnOrderItem> findAllById(Long id);

	List<ReturnOrderItem> findByReturnOrderId(Long returnOrderId);

}