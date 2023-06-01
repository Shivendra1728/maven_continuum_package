package com.continuum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface ReturnOrderService {

	public String createReturnOrder(ReturnOrderDTO returnOrderDTO);

	public List<ReturnOrderDTO> getReturnOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
}
