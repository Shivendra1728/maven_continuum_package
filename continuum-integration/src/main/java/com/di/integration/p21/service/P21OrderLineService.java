package com.di.integration.p21.service;

import java.util.List;

import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface P21OrderLineService {
	
	public String getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters);

}

