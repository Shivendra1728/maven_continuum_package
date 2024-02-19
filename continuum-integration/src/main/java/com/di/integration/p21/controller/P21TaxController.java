package com.di.integration.p21.controller;

import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21TaxService;

@RestController
@RequestMapping("/P21/tax")
public class P21TaxController {
	
	@Autowired
	P21TaxService p21TaxService;

	@GetMapping("/get")
	public Map<String, String> getTax(@RequestParam(required = true) String rmaNo) throws URISyntaxException, Exception {
		return p21TaxService.getTax(rmaNo,null);
	}

}
