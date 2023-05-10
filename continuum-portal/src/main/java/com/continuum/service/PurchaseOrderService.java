package com.continuum.service;

import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.dto.ReturnOrderDTO;

public interface PurchaseOrderService {
	
	public PurchaseOrderDTO getOrdersById(Long id);

	public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(String zipcode, String invoiceNo);


	public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(String zipcode, String poNo);

	public String createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);

	public PurchaseOrderDTO getOrdersByCustomerIdAndInvoiceNo(Long customerId, String invoiceNo);

	public PurchaseOrderDTO getOrdersByCustomerIdAndPONumber(Long customerId, String poNo);

}
