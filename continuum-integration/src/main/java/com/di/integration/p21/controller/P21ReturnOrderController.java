package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.ReturnOrderDTO;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.P21RMAResponse;

@RestController
@RequestMapping("/P21/returnOrder")
public class P21ReturnOrderController {

	@Autowired
	P21ReturnOrderService p21Service;
	
	@PostMapping("/create/v1")
	public P21RMAResponse createReturnOrder(@RequestBody ReturnOrderDTO returnOrderDTO) throws Exception {
		return p21Service.createReturnOrder(returnOrderDTO);
		
	}
	
	@PostMapping("/link/invoice")
	public P21RMAResponse linkInvoice() throws Exception {
		return p21Service.linkInvoice();
		
	}
	
	/*
	 * @GetMapping("/search") public List<ReturnOrderDTO>
	 * getReturnOrdersBySearchCriteria(@RequestParam(required = false) String
	 * zipcode,
	 * 
	 * @RequestParam(required = false) String poNo, @RequestParam(required = false)
	 * String customerId,
	 * 
	 * @RequestParam(required = false) String invoiceNo) { OrderSearchParameters
	 * orderSearchParameters = new OrderSearchParameters();
	 * orderSearchParameters.setZipcode(zipcode);
	 * orderSearchParameters.setPoNo(poNo);
	 * orderSearchParameters.setCustomerId(customerId);
	 * orderSearchParameters.setInvoiceNo(invoiceNo); return
	 * returnOrderService.getReturnOrdersBySearchCriteria(orderSearchParameters); }
	 */
}
