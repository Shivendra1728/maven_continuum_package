package com.continuum.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.mapper.CustomerMapper;

@Service
public class CustomerServiceImpl implements CustomerService{

	@Autowired
	CustomerRepository repo;
	
	@Autowired
	CustomerMapper customerMapper;
	
	public CustomerDTO findbyCustomerId(String customerId){
	Customer customer=	repo.findByCustomerId(customerId);
	return customerMapper.cusotmerTocusotmerDTO(customer);
	}
	
	public CustomerDTO createCustomer(CustomerDTO custDTO){
		return customerMapper.cusotmerTocusotmerDTO(repo.save(customerMapper.cusotmerDTOTocusotmer(custDTO)));
	}
}
