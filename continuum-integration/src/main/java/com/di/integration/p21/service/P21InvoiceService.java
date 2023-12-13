package com.di.integration.p21.service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;

public interface P21InvoiceService {
	
		public boolean linkInvoice(String rmaNo,MasterTenant masterTenant) throws Exception;
}
