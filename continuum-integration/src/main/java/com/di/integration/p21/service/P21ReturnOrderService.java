package com.di.integration.p21.service;

import com.di.commons.dto.ReturnOrderDTO;
import com.di.integration.p21.transaction.P21RMAResponse;

public interface P21ReturnOrderService {

	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception;

	public P21RMAResponse linkInvoice();

}
