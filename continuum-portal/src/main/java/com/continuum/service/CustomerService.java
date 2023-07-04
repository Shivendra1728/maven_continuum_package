package com.continuum.service;

import com.di.commons.dto.CustomerDTO;

public interface CustomerService {

	public CustomerDTO findbyCustomerId(String customerId);
		
		public CustomerDTO createCustomer(CustomerDTO custDTO);
}
