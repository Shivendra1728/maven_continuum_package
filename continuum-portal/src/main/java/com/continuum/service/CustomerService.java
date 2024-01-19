package com.continuum.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.di.commons.dto.CustomerDTO;

public interface CustomerService {
	public CustomerDTO findbyCustomerId(String customerId);

	public CustomerDTO createCustomer(CustomerDTO custDTO) throws Exception;

	public Map<String, Object> createCustomerInDB(CustomerDTO customerDTO, HttpServletRequest request);
}
