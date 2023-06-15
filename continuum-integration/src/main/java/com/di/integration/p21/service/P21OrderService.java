package com.di.integration.p21.service;

import java.util.List;

import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface P21OrderService {

	public String getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
}
