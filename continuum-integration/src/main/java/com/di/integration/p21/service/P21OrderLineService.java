package com.di.integration.p21.service;

import java.text.ParseException;
import java.util.List;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.transaction.SerialData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface P21OrderLineService {
	
	
	public List<SerialData> getSerialNumbers(String orderNo, String lineNo) throws Exception;
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters,
			MasterTenant masterTenantObject, int totalItem, String invoiceNo)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception;
}

