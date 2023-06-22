package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.ReturnOrderDTO;
import com.di.integration.p21.service.P21ReturnOrderService;

@RestController
@RequestMapping("/P21/returnOrder")
public class P21ReturnOrderController {

	@Autowired
	P21ReturnOrderService p21Service;
	
	@PostMapping("/create/v1")
	public String createReturnOrder(@RequestBody ReturnOrderDTO returnOrderDTO) throws Exception {
		return p21Service.createReturnOrder(returnOrderDTO);
		
	}
}
