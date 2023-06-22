package com.di.integration.p21.service;

import java.text.ParseException;
import java.util.List;

import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface P21OrderLineService {
	
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters) throws JsonMappingException, JsonProcessingException, ParseException, Exception;

}

