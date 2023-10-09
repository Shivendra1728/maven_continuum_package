package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.Invoice;

public interface InvoiceService {
	
	public List<Invoice> getInvoice(String customerId);

}
