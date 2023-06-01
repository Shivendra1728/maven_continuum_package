package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.OrderAddress;
import com.continuum.repos.entity.Orders;
import com.continuum.repos.repositories.OrderRepository;
import com.continuum.service.OrderService;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.mapper.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderMapper orderMapper;
	

	@Override
	public String createOrder(OrderDTO orderDTO) {
		Orders orders= orderMapper.orderDTOToOrder(orderDTO);
		orderRepository.save(orders);
		return "PO created succssfully";
	}
	
	@Override
	public List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) {
		
		Specification<Orders> spec = Specification.where(null);

		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
		    Specification<Orders> zipcodeSpec = (root, query, builder) -> {
		        Join<Orders, OrderAddress> addressJoin = root.join("billTo");
		        Predicate zipcodePredicate = builder.equal(addressJoin.get("zipcode"), orderSearchParameters.getZipcode());
		        return builder.and(zipcodePredicate);
		    };
		    spec = spec.and(zipcodeSpec);
		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
		    Specification<Orders> customerIdSpec = (root, query, builder) -> {
		        Join<Orders, OrderAddress> addressJoin = root.join("customer");
		        Predicate customerIdPredicate = builder.equal(addressJoin.get("customerId"), orderSearchParameters.getCustomerId());
		        return builder.and(customerIdPredicate);
		    };
		    spec = spec.and(customerIdSpec);
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getPoNo())) {
		    Specification<Orders> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("PONumber"), orderSearchParameters.getPoNo());
		    spec = spec.and(poNoSpec);
		}
		
		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
		    Specification<Orders> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("invoiceNo"), orderSearchParameters.getInvoiceNo());
		    spec = spec.and(poNoSpec);
		}

		List<Orders> poList= orderRepository.findAll(spec);
		List<OrderDTO> poDTOList= new ArrayList<>();
		poList.forEach(purchaseOrder-> {
			poDTOList.add(orderMapper.orderToOrderDTO(purchaseOrder));
		});
		return poDTOList;
	}
	
	public boolean isNotNullAndNotEmpty(String str) {
	    return str != null && !str.trim().isEmpty();
	}
}
