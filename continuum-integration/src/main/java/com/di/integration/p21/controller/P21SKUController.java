package com.di.integration.p21.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21SKUService;

@RestController
@RequestMapping("/P21/SKU")
public class P21SKUController {
	
	@Autowired
	P21SKUService p21SKUService;
	
	@DeleteMapping("/delete")
	public String skuDelete(@RequestParam(required = true) String itemId,@RequestParam(required = true) String rmaNo) throws URISyntaxException, Exception {
		 return p21SKUService.deleteSKU(itemId,rmaNo,null);
	}

}
