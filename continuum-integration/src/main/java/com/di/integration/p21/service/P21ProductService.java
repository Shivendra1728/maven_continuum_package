package com.di.integration.p21.service;

import com.di.commons.dto.OrderDTO;

public interface P21ProductService {

	OrderDTO getProductByProductId(String productId);

}
