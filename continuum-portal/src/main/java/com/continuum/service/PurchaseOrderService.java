package com.continuum.service;

import java.util.List;

import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface PurchaseOrderService {
	
	
	public List<PurchaseOrderDTO>  getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
	
	public String createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);
	
	
	/*
	 * public PurchaseOrderDTO getOrdersById(Long id);
	 * 
	 * public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(String zipcode, String
	 * invoiceNo);
	 * 
	 * 
	 * public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(String zipcode, String
	 * poNo);
	 * 
	 * public PurchaseOrderDTO getOrdersByCustomerIdAndInvoiceNo(Long customerId,
	 * String invoiceNo);
	 * 
	 * public PurchaseOrderDTO getOrdersByCustomerIdAndPONumber(Long customerId,
	 * String poNo);
	 */



}
