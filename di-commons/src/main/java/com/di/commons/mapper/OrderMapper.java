package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Orders;
import com.di.commons.dto.OrderDTO;

@Component
public class OrderMapper {

	@Autowired
	private ModelMapper modelMapper;

	public OrderDTO orderToOrderDTO(Orders orders) {

		OrderDTO poDTO = modelMapper.map(orders, OrderDTO.class);
		return poDTO;
	}

	public Orders orderDTOToOrder(OrderDTO orderDTO) {

		Orders po = modelMapper.map(orderDTO, Orders.class);
		return po;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
	}
}
