package com.continuum.tenant.repos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;

@Repository
public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long>, JpaSpecificationExecutor<ReturnOrder>{

	List<ReturnOrder> findByrmaOrderNo(String rmaOrderNo);

	List<ReturnOrder> findByUserId(Long id);

	Optional<ReturnOrder> findByRmaOrderNo(String rmaNo);
	ReturnOrder findFirstByRmaOrderNoStartingWithOrderByRmaOrderNoDesc(String string);
}
