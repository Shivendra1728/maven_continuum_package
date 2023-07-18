package com.continuum.service;
import java.util.List;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface OrderService {
	public List<OrderDTO>  getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters);
	public String createOrder(OrderDTO orderDTO);
}
