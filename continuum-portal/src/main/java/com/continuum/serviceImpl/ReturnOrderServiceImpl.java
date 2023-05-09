package com.continuum.serviceImpl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.ReturnOrder;
import com.continuum.repos.repositories.ReturnOrderRepository;
import com.continuum.service.ReturnOrderService;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.mapper.ReturnOrderMapper;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService{
	
	@Autowired
	ReturnOrderRepository repository;
	
	@Autowired
	ReturnOrderMapper returnOrderMapper;
	
	public String createReturnOrder(ReturnOrderDTO returnOrderDTO) {
		ReturnOrder returnOrder= returnOrderMapper.ReturnOrderDTOToReturnOrder(returnOrderDTO);
		repository.save(returnOrder);
		return "Order returned successfully";
	}

}
