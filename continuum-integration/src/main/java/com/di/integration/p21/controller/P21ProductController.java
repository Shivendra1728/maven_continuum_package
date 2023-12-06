package com.di.integration.p21.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.OrderDTO;
import com.di.integration.p21.service.P21ProductService;

@RestController
@RequestMapping("P21/Product")
public class P21ProductController {
	
	@Autowired
	P21ProductService p21ProductService; 

	@GetMapping("/search")
	public OrderDTO getProductByProductId(@RequestParam(required = true) String productId){
		return p21ProductService.getProductByProductId(productId);

 

	}

}
