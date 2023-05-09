package com.di.commons.mapper;

import com.continuum.repos.entity.OrderAddress;
import com.continuum.repos.entity.PurchaseOrder;
import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.PurchaseOrderDTO;

public class PurchaseOrderMapper {
	
	
	
	public static PurchaseOrderDTO PurchaseOrderToPurchaseOrderDTO(PurchaseOrder purchaseOrder) {
		
		PurchaseOrderDTO poDTO= new PurchaseOrderDTO();
		poDTO.setPONumber(purchaseOrder.getPONumber());
		poDTO.setId(purchaseOrder.getId());
		poDTO.setBillTo(OrderAddressToOrderAddressDTO(purchaseOrder.getBillTo()));
		poDTO.setShipTo(OrderAddressToOrderAddressDTO(purchaseOrder.getShipTo()));
		purchaseOrder.getId();
	
	return poDTO;
	}

	

	public static OrderAddressDTO OrderAddressToOrderAddressDTO(OrderAddress orderAddress) {
		
		OrderAddressDTO orderAddressDTO= new OrderAddressDTO();
		orderAddressDTO.setId(orderAddress.getId());
		orderAddressDTO.setCountry(orderAddress.getCountry());
		orderAddressDTO.setProvince(orderAddress.getProvince());
		orderAddressDTO.setAddressType(orderAddress.getAddressType());
		orderAddressDTO.setPhoneNumber(orderAddress.getPhoneNumber());
		orderAddressDTO.setStreet1(orderAddress.getStreet1());
		orderAddressDTO.setStreet2(orderAddress.getStreet2());
		orderAddressDTO.setZipcode(orderAddress.getZipcode());
		orderAddressDTO.setFax(orderAddress.getFax());
	
		return orderAddressDTO;
	}
}
