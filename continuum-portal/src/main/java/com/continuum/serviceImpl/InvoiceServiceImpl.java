package com.continuum.serviceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<Invoice> getInvoice(String customerId) {
	    List<Invoice> invoices = invoiceRepository.findByCustomerId(customerId);

	    Calendar oneYearAgo = Calendar.getInstance();
	    oneYearAgo.add(Calendar.YEAR, -1);

	    List<Invoice> activeInvoices = invoices.stream()
	        .filter(invoice -> invoice.getIsActive() && isWithinLastYear(invoice.getCreatedDate(), oneYearAgo.getTime()))
	        .collect(Collectors.toList());

	    return activeInvoices;
	}

	private boolean isWithinLastYear(Date date, Date oneYearAgo) {
	    return date.after(oneYearAgo);
	}
	

}
