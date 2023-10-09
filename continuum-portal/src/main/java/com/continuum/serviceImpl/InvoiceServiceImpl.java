package com.continuum.serviceImpl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.InvoiceService;
import com.continuum.tenant.repos.entity.Invoice;
import com.continuum.tenant.repos.repositories.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService {
	
	@Autowired
	InvoiceRepository invoiceRepository;
	
	@Override
	public List<Invoice> getInvoice(String customerId){
		return invoiceRepository.findByCustomerId(customerId);	
	}
	

}
