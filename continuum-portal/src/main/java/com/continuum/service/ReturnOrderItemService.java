package com.continuum.service;

import java.math.BigDecimal;

import com.continuum.tenant.repos.entity.OrderAddress;
import com.di.commons.dto.ReturnOrderItemDTO;

public interface ReturnOrderItemService {
	String updateReturnOrderItem(Long id, ReturnOrderItemDTO updatedItem);

	String updateNote(Long lineItemId, Long assignToId, String rmaNo,ReturnOrderItemDTO updateNote);

	String updateShipTo(Long rtnOrdId, OrderAddress orderAddress);

	String updateRestockingFee(Long id, BigDecimal reStockingAmount, ReturnOrderItemDTO returnOrderItemDTO);
}
