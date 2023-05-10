package com.continuum.serviceImpl;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.PurchaseOrder;
import com.continuum.repos.entity.ReturnOrder;
import com.continuum.repos.repositories.PurchaseOrderRepository;
import com.continuum.repos.repositories.ReturnOrderRepository;
import com.continuum.service.PurchaseOrderService;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.exceptions.ResourceNotFoundException;
import com.di.commons.mapper.PurchaseOrderMapper;
import com.di.commons.mapper.ReturnOrderMapper;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	@Autowired
	PurchaseOrderRepository orderRepository;
	
	@Autowired
	PurchaseOrderMapper purchaseOrderMapper;
	

	@Override
	public PurchaseOrderDTO getOrdersById(Long id) {
		PurchaseOrder po= orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
		return purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}

	public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(String zipcode, String invoiceNo) {
		PurchaseOrder po= orderRepository.getOrdersByInvoiceNoAndBillTo_ZipcodeOrShipTo_Zipcode(invoiceNo,zipcode,zipcode).orElseThrow(() -> new EntityNotFoundException());
		return purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}


	@Override
	public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(String zipcode, String poNo) {
		PurchaseOrder po= orderRepository.getOrdersByPONumberAndBillTo_Zipcode(poNo,zipcode).orElseThrow(() -> new EntityNotFoundException());
		return purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}

	@Override
	public String createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
		PurchaseOrder purchaseOrder= purchaseOrderMapper.PurchaseOrderDTOToPurchaseOrder(purchaseOrderDTO);
		orderRepository.save(purchaseOrder);
		return "PO created succssfully";
	}

	@Override
	public PurchaseOrderDTO getOrdersByCustomerIdAndInvoiceNo(Long customerId, String invoiceNo) {
		PurchaseOrder po= orderRepository.getOrdersByCustomerIdAndInvoiceNo(customerId,invoiceNo).orElseThrow(() -> new EntityNotFoundException());
		return purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}

	@Override
	public PurchaseOrderDTO getOrdersByCustomerIdAndPONumber(Long customerId, String poNo) {
		PurchaseOrder po= orderRepository.getOrdersByCustomerIdAndPONumber(customerId,poNo).orElseThrow(() -> new EntityNotFoundException());
		return purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(po);
	}
	
	
}
