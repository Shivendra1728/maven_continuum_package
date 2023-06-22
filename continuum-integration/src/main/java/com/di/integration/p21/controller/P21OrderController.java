package com.di.integration.p21.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.p21.service.P21OrderService;

@RestController
@RequestMapping("/P21/order")
public class P21OrderController {
	
	@Autowired
	P21OrderService orderService;

	@GetMapping("/search")
	public List<OrderDTO> getOrdersBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String poNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String invoiceNo) throws Exception {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setZipcode(zipcode);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		return orderService.getOrdersBySearchCriteria(orderSearchParameters);
		
	}
}
