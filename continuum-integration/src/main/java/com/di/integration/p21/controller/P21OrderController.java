package com.di.integration.p21.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.service.P21OrderService;

@RestController
@RequestMapping("/P21/order")
public class P21OrderController {
	
	@Autowired
	P21OrderService orderService;

	@GetMapping("/search")
	public List<OrderDTO> getOrdersBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String poNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String invoiceNo,@RequestParam(required = false) String orderNo) throws Exception {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setZipcode(zipcode);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		orderSearchParameters.setOrderNo(orderNo);
		return orderService.getOrdersBySearchCriteria(orderSearchParameters);
		
	}
}
