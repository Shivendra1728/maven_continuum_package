package com.di.commons.p21.mapper;

import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderDTO;

@Component
public class P21OrderMapper {

	
	
	public OrderDTO convertP21OrderObjectToOrderDTO(Object order) {

		OrderDTO poDTO = new OrderDTO();
		return poDTO;
	}
}
