package com.continuum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.transaction.P21RMAResponse;

public interface ReturnOrderService {

	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception;

	public List<ReturnOrderDTO> getReturnOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
}
