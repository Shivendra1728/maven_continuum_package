package com.di.commons.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.repos.entity.PurchaseOrder;
import com.continuum.repos.entity.PurchaseOrderItem;
import com.continuum.repos.entity.ReturnOrder;
import com.continuum.repos.entity.ReturnOrderItem;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.dto.ReturnOrderDTO;

@Component
public class PurchaseOrderMapper {
	
	@Autowired
    private ModelMapper modelMapper;
	
	
	public PurchaseOrderDTO PurchaseOrderToPurchaseOrderDTO(PurchaseOrder purchaseOrder) {
		
		PurchaseOrderDTO poDTO= modelMapper.map(purchaseOrder, PurchaseOrderDTO.class);
	return poDTO;
	}

	public PurchaseOrder PurchaseOrderDTOToPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
		
		PurchaseOrder po= modelMapper.map(purchaseOrderDTO, PurchaseOrder.class);
		po.setPurchaseOrderItems(mapList(purchaseOrderDTO.getPurchaseOrderItemsDtos(), PurchaseOrderItem.class));
	return po;
	}
	
	
	public  ReturnOrderDTO ReturnOrderToReturnOrderDTO(ReturnOrder returnOrder) {
		
		ReturnOrderDTO poDTO=  modelMapper.map(returnOrder, ReturnOrderDTO.class);
		returnOrder.getId();
	
	return poDTO;
	}


	public  ReturnOrder ReturnOrderDTOToReturnOrder(ReturnOrderDTO returnOrderDTO) {
		ReturnOrder returOrder;
		returOrder = modelMapper.map(returnOrderDTO, ReturnOrder.class);
		returOrder.setReturnOrderItem(mapList(returnOrderDTO.getReturnOrderItemDTOList(), ReturnOrderItem.class));
	return returOrder;
	}

	
	<S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
	    return source
	      .stream()
	      .map(element -> modelMapper.map(element, targetClass))
	      .collect(Collectors.toList());
	}
}
