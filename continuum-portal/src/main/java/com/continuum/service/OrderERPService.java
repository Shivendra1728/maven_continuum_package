package com.continuum.service;

import java.util.List;

import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface OrderERPService {
	List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
}
