package com.continuum.service;

import com.di.commons.dto.ReturnOrderItemDTO;

public interface ReturnOrderItemService {
    String updateReturnOrderItem(Long id, ReturnOrderItemDTO updatedItem);
}
