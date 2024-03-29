package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.ReturnOrder;
import com.di.commons.dto.ReturnDTO;
import com.di.commons.dto.ReturnOrderDTO;

@Component
public class ReturnOrderMapper {

	@Autowired
	private ModelMapper modelMapper;

	public ReturnOrderDTO returnOrderToReturnOrderDTO(ReturnOrder returnOrder) {

		ReturnOrderDTO poDTO = modelMapper.map(returnOrder, ReturnOrderDTO.class);
		returnOrder.getId();

		return poDTO;
	}
	
	public ReturnDTO returnOrderToReturnDTO(ReturnOrder returnOrder) {

		ReturnDTO poDTO = modelMapper.map(returnOrder, ReturnDTO.class);
		returnOrder.getId();

		return poDTO;
	}

	public ReturnOrder returnOrderDTOToReturnOrder(ReturnOrderDTO returnOrderDTO) {
		ReturnOrder returOrder;
		returOrder = modelMapper.map(returnOrderDTO, ReturnOrder.class);
		// returOrder.setReturnOrderItem(mapList(returnOrderDTO.getReturnOrderItemDTOList(),
		// ReturnOrderItem.class));
		return returOrder;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
	}
}
