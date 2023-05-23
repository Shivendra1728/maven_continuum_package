package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.repos.entity.Orders;
import com.continuum.repos.entity.OrderItem;
import com.continuum.repos.entity.ReturnOrder;
import com.continuum.repos.entity.ReturnOrderItem;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReturnOrderDTO;

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

	public ReturnOrderDTO returnOrderToReturnOrderDTO(ReturnOrder returnOrder) {

		ReturnOrderDTO poDTO = modelMapper.map(returnOrder, ReturnOrderDTO.class);
		returnOrder.getId();

		return poDTO;
	}

	public ReturnOrder returnOrderDTOToReturnOrder(ReturnOrderDTO returnOrderDTO) {
		ReturnOrder returOrder;
		returOrder = modelMapper.map(returnOrderDTO, ReturnOrder.class);
		returOrder.setReturnOrderItem(mapList(returnOrderDTO.getReturnOrderItemDTOList(), ReturnOrderItem.class));
		return returOrder;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
	}
}
