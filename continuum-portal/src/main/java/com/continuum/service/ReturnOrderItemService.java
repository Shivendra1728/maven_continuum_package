package com.continuum.service;

import com.continuum.tenant.repos.entity.OrderAddress;
import com.di.commons.dto.ReturnOrderItemDTO;

public interface ReturnOrderItemService {
	String updateReturnOrderItem(Long id, ReturnOrderItemDTO updatedItem);

	String updateNote(Long lineItemId, Long assignToId, ReturnOrderItemDTO updateNote);

	String updateShipTo(Long rtnOrdId, OrderAddress orderAddress);
}
