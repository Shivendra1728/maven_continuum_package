package com.di.commons.mapper;

import com.continuum.repos.entity.ReturnOrderItem;
import com.di.commons.dto.ReturnOrderItemDTO;

public class ReturnOrderItemMapper {
	

	public static ReturnOrderItem ReturnOrderDTOToReturnOrder(ReturnOrderItemDTO returnOrderItemDTO) {
		
		ReturnOrderItem returnOrderItem= new ReturnOrderItem();
		returnOrderItem.setQuanity(returnOrderItemDTO.getQuanity());
		returnOrderItem.setId(returnOrderItemDTO.getId());
		returnOrderItem.setProblemDesc(returnOrderItemDTO.getProblemDesc());
		returnOrderItem.setStatus(returnOrderItemDTO.getStatus());
	//	returnOrderItem.setReasonCode(returnOrderItemDTO.getReasonCode());
		//poDTO.setBillTo(OrderAddressToOrderAddressDTO(returnOrder.getBillTo()));
		//poDTO.setShipTo(OrderAddressToOrderAddressDTO(returnOrder.getShipTo()));
		//returnOrder.getId();
	
	return returnOrderItem;
	}

}
