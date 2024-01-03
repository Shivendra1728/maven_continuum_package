package com.di.integration.p21.service;

import java.text.ParseException;
import java.util.List;

import com.di.commons.dto.OrderDTO;

public interface P21ProductService {

	public List<OrderDTO> getProductByProductId(String productId, String customerId) throws ParseException, Exception;

}
