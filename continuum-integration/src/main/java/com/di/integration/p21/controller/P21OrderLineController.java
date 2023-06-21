package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.p21.service.P21OrderLineService;

@RestController
@RequestMapping("/P21/search")
public class P21OrderLineController {

	@Autowired
	P21OrderLineService p21orderlineservice;

	@Autowired
	P21OrderMapper p21ordermapper;

	@GetMapping("/lineorder")
	public OrderDTO getOrdersLineBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String invoiceNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String poNo) {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setZipcode(zipcode);
		return p21ordermapper.convertP21OrderObjectToOrderDTO(
				p21orderlineservice.getordersLineBySearchcriteria(orderSearchParameters));
	}
}
