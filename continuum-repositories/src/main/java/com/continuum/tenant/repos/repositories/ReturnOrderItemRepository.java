package com.continuum.tenant.repos.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.continuum.tenant.repos.entity.ReturnOrderItem;

public interface ReturnOrderItemRepository extends JpaRepository<ReturnOrderItem, Long> {

	List<ReturnOrderItem> findByReturnOrderId(Object returnOrder);

	List<ReturnOrderItem> findAllById(Long id);

	List<ReturnOrderItem> findByIdIn(List<Long> ids);

	@Transactional
    @Modifying
    @Query(value = "UPDATE return_order_item SET return_order_id = :returnOrderId WHERE id = :itemId", nativeQuery = true)
    void updateReturnOrder(@Param("itemId") Long itemId, @Param("returnOrderId") Long returnOrderId);

}
