package com.continuum.service;

import com.di.commons.dto.PurchaseOrderDTO;

public interface PurchaseOrderService {
	
	public PurchaseOrderDTO getOrdersById(Long id);

	public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(String zipcode, String invoiceNo);


	public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(String zipcode, String poNo);

}
