package com.di.commons.mapper;

import com.continuum.repos.entity.ReturnOrder;
import com.di.commons.dto.ReturnOrderDTO;

public class ReturnOrderMapper {
	
	

	public static ReturnOrderDTO ReturnOrderToReturnOrderDTO(ReturnOrder returnOrder) {
		
		ReturnOrderDTO poDTO= new ReturnOrderDTO();
		poDTO.setPONumber(returnOrder.getPONumber());
		poDTO.setId(returnOrder.getId());
		//poDTO.setBillTo(OrderAddressToOrderAddressDTO(returnOrder.getBillTo()));
		//poDTO.setShipTo(OrderAddressToOrderAddressDTO(returnOrder.getShipTo()));
		returnOrder.getId();
	
	return poDTO;
	}


	public static ReturnOrder ReturnOrderDTOToReturnOrder(ReturnOrderDTO returnOrderDTO) {
		
		ReturnOrder returnOrder= new ReturnOrder();
		returnOrder.setPONumber(returnOrderDTO.getPONumber());
		returnOrder.setId(returnOrderDTO.getId());
		//poDTO.setBillTo(OrderAddressToOrderAddressDTO(returnOrder.getBillTo()));
		//poDTO.setShipTo(OrderAddressToOrderAddressDTO(returnOrder.getShipTo()));
		returnOrder.getId();
	
	return returnOrder;
	}

}
