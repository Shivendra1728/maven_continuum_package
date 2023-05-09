package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.PurchaseOrderService;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.dto.ReturnOrderDTO;

@RestController
public class PurchaseOrdercontroller {
	@Autowired
	PurchaseOrderService poService;
	
	@GetMapping("/order/{id}")
	public PurchaseOrderDTO getOrdersById(@PathVariable Long id) {
	return poService.getOrdersById(id);
	}
	
	@GetMapping("/order/zipAndInvoice/{zipcode}/{invoiceNo}")
	public PurchaseOrderDTO getOrdersByZipCodeAndInvoiceNo(@PathVariable String zipcode,@PathVariable String invoiceNo) {
	return poService.getOrdersByZipCodeAndInvoiceNo(zipcode,invoiceNo);
	}
	
	@GetMapping("/order/zipAndPO/{zipcode}/{poNo}")
	public PurchaseOrderDTO getOrdersByZipcodeAndPONumber(@PathVariable String zipcode,@PathVariable String poNo) {
	return poService.getOrdersByZipcodeAndPONumber(zipcode,poNo);
	}
	
	@PostMapping("/createPurchaseOrder/v1")
	public String createPurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
		return poService.createPurchaseOrder(purchaseOrderDTO);
		
	}

}
