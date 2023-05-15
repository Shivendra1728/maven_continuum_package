package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.PurchaseOrderService;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.helper.OrderSearchParameters;

@RestController
@RequestMapping("/PO")
public class PurchaseOrderController {
	@Autowired
	PurchaseOrderService poService;

	@GetMapping("/search")
	public List<PurchaseOrderDTO> getOrdersBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String poNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String invoiceNo) {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setZipcode(zipcode);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		return poService.getOrdersBySearchCriteria(orderSearchParameters);
	}
	
	  @PostMapping("/create/v1") public String
	  createPurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) { return
	  poService.createPurchaseOrder(purchaseOrderDTO);
	  
	  }
	 
}
