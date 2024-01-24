package com.di.commons.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Customer;
import com.di.commons.dto.CustomerDTO;

@Component
public class CustomerMapper {

	@Autowired
	private ModelMapper modelMapper;

	public CustomerDTO cusotmerTocusotmerDTO(Customer customer) {
		if (customer != null) {
			CustomerDTO custDTO = modelMapper.map(customer, CustomerDTO.class);
			return custDTO;
		}
		return null;
	}

	public Customer cusotmerDTOTocusotmer(CustomerDTO custDTO) {
		Customer cust = modelMapper.map(custDTO, Customer.class);
		return cust;
	}
}
