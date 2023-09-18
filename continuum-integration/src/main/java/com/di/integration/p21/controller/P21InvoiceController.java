package com.di.integration.p21.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.integration.p21.service.P21InvoiceService;


@RestController
@RequestMapping("/P21/invoice")
public class P21InvoiceController {
	@Autowired
	P21InvoiceService invoiceService;

	@GetMapping("/link")
	public void linkInvoice(@RequestParam(required = true) String rmaNo) throws URISyntaxException, Exception {
		invoiceService.linkInvoice(rmaNo);
	}
}