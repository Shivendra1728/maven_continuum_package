package com.continuum.serviceImpl;

import java.util.Calendar;
import java.util.Collections;
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
	public List<Invoice> getInvoice(String customerId, String poNo, String invoiceNo, String orderNo) {
	    if (customerId == null && poNo == null && invoiceNo == null && orderNo == null) {
	        return Collections.emptyList();
	    }

	    Calendar oneYearAgo = Calendar.getInstance();
	    oneYearAgo.add(Calendar.YEAR, -1);

	    List<Invoice> activeInvoices = invoiceRepository.findAll()
	            .stream()
	            .filter(invoice ->
	                    (customerId == null || customerId.equals(invoice.getCustomerId())) &&
	                    (poNo == null || poNo.equals(invoice.getPoNo())) &&
	                    (invoiceNo == null || invoiceNo.equals(invoice.getInvNo())) &&
//	                    (orderNo == null || orderNo.equals(invoice.getOrderNo())) &&
	                    invoice.getIsActive() &&
	                    isWithinLastYear(invoice.getCreatedDate(), oneYearAgo.getTime()))
	            .collect(Collectors.toList());

	    return activeInvoices;
	}



	private boolean isWithinLastYear(Date date, Date oneYearAgo) {
		return date.after(oneYearAgo);
	}

}
