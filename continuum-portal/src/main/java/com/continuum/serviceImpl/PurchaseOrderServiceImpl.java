package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.PurchaseOrder;
import com.continuum.repos.repositories.PurchaseOrderRepository;
import com.continuum.service.PurchaseOrderService;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.mapper.PurchaseOrderMapper;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	@Autowired
	PurchaseOrderRepository orderRepository;
	

	@Override
	public PurchaseOrderDTO getOrdersById(Long id) {
		PurchaseOrder po= orderRepository.findById(id).orElse(new PurchaseOrder());
		return PurchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}

	public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(String zipcode, String invoiceNo) {
		PurchaseOrder po= orderRepository.getOrdersByInvoiceNoAndBillTo_ZipcodeOrShipTo_Zipcode(invoiceNo,zipcode,zipcode).orElse(new PurchaseOrder());
		return PurchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}


	@Override
	public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(String zipcode, String poNo) {
		PurchaseOrder po= orderRepository.getOrdersByPONumberAndBillTo_Zipcode(poNo,zipcode).orElse(new PurchaseOrder());
		return PurchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}
	
}
