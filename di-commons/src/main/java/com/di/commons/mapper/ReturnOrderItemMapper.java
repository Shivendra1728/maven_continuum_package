package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.di.commons.dto.ReturnOrderItemDTO;

@Component
public class ReturnOrderItemMapper {

	@Autowired
	private ModelMapper modelMapper;

	public ReturnOrderItemDTO returnOrderItemToReturnOrderItemDTO(ReturnOrderItem returnOrderItem) {

		ReturnOrderItemDTO poDTO = modelMapper.map(returnOrderItem, ReturnOrderItemDTO.class);
		returnOrderItem.getId();

		return poDTO;
	}

	public ReturnOrderItem returnOrderItemDTOToReturnOrderItem(ReturnOrderItemDTO returnOrderItemDTO) {
		ReturnOrderItem returOrderItem;
		returOrderItem = modelMapper.map(returnOrderItemDTO, ReturnOrderItem.class);
		// returOrder.setReturnOrderItem(mapList(returnOrderDTO.getReturnOrderItemDTOList(),
		// ReturnOrderItem.class));
		return returOrderItem;
	}

	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
		return source.stream().map(element -> modelMapper.map(element, targetClass)).collect(Collectors.toList());
	}
}