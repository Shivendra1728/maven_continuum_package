package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.InvoiceService;
import com.continuum.tenant.repos.entity.Invoice;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
	
	@Autowired
	InvoiceService invoiceService;
	
	@GetMapping("/search")
	public List<Invoice> getInvoice(@RequestParam(required = false) String customerId , @RequestParam(required = false) String poNo , @RequestParam(required = false) String invoiceNo,@RequestParam(required = false) String orderNo){
		return invoiceService.getInvoice(customerId,poNo,invoiceNo,orderNo);

	}
}
