package com.continuum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnOrderService;
import com.di.commons.dto.ReturnOrderDTO;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {
	
	@Autowired
	ReturnOrderService returnOrderService;
	
	@PostMapping("/create/v1")
	public String createReturnOrder(@RequestBody ReturnOrderDTO returnOrderDTO) {
		return returnOrderService.createReturnOrder(returnOrderDTO);
		
	}
}
