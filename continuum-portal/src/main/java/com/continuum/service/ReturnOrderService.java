package com.continuum.service;

import java.util.List;

import javax.mail.MessagingException;

import com.continuum.tenant.repos.entity.ReturnOrder;
import com.di.commons.dto.ReturnDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.transaction.P21RMAResponse;

public interface ReturnOrderService {
	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception;

	public List<ReturnOrderDTO> getReturnOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);

	// @Async
	public void crateReturnOrderInDB(ReturnOrderDTO returnOrderDTO, P21RMAResponse p21RMARespo , Long userId)
			throws MessagingException;

	public List<ReturnDTO> getAllReturnOrder(Long userId);

	public List<ReturnOrderDTO> getAllReturnOrderByRmaNo(String rmaOrderNo);

	public String updateReturnOrder(String rmaNo, String updateBy, String status);

	public String getSearchRmaInvoiceinfo() throws Exception;

	public String assignRMA(String rmaNo, Long assignToId, String updateBy, Long returnTypeId, ReturnOrderDTO note);
}
