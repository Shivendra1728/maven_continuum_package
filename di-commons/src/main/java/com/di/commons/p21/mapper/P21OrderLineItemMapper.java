package com.di.commons.p21.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.P21OrderDataHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class P21OrderLineItemMapper {
	@Autowired
	private final ObjectMapper objectMapper;
	public P21OrderLineItemMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	public OrderDTO convertP21OrderObjectToOrderDTO(String item) {
try {
			
			P21OrderLineItemHelper p21OrderLineItemHelper = objectMapper.readValue(item, P21OrderLineItemHelper.class);
	}
	
}
