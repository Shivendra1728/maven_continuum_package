package com.continuum.service;

import org.springframework.stereotype.Service;

import com.di.commons.dto.ReturnOrderDTO;

public interface ReturnOrderService {

	public String createReturnOrder(ReturnOrderDTO returnOrderDTO);
}
