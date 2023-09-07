package com.di.integration.p21.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.service.P21OrderLineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
@RequestMapping("/P21/search")
public class P21OrderLineController {

	@Autowired
	P21OrderLineService p21orderLineService;


	@GetMapping("/orderItem")
	public List<OrderItemDTO> getOrdersLineBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String invoiceNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String orderNo) throws JsonMappingException, JsonProcessingException, ParseException, Exception {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		orderSearchParameters.setOrderNo(orderNo);
		orderSearchParameters.setZipcode(zipcode);
		return p21orderLineService.getordersLineBySearchcriteria(orderSearchParameters,-1,invoiceNo);

	}

}