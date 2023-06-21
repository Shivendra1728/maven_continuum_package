package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.p21.service.P21OrderLineService;

@RestController
@RequestMapping("/P21/search")
public class P21OrderLineController {

	@Autowired
	P21OrderLineService p21orderLineService;

	@Autowired
	P21OrderLineItemMapper p21orderMapperItemMapper;

	@GetMapping("/orderItem")
	public OrderItemDTO getOrdersLineBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String invoiceNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String poNo) {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setZipcode(zipcode);
		p21orderLineService.getordersLineBySearchcriteria(orderSearchParameters);
		//return p21orderMapperItemMapper.convertP21OrderItemObjectToOrderItemDTO();
		return new OrderItemDTO();
	}

}
