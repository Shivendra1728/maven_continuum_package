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
import com.continuum.repos.repositories.PurchaseOrderRepository;
import com.continuum.service.OrderService;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.mapper.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	PurchaseOrderRepository orderRepository;
	
	@Autowired
	OrderMapper orderMapper;
	

	@Override
	public String createOrder(OrderDTO orderDTO) {
		Orders orders= orderMapper.OrderDTOToOrder(orderDTO);
		orderRepository.save(orders);
		return "PO created succssfully";
	}
	
	@Override
	public List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) {
		
		Specification<Orders> spec = Specification.where(null);

		if (orderSearchParameters.getZipcode() != null) {
		    Specification<Orders> zipcodeSpec = (root, query, builder) -> {
		        Join<Orders, OrderAddress> addressJoin = root.join("billTo");
		        Predicate zipcodePredicate = builder.equal(addressJoin.get("zipcode"), orderSearchParameters.getZipcode());
		        return builder.and(zipcodePredicate);
		    };
		    spec = spec.and(zipcodeSpec);
		}
		if (orderSearchParameters.getCustomerId() != null) {
		    Specification<Orders> customerIdSpec = (root, query, builder) -> {
		        Join<Orders, OrderAddress> addressJoin = root.join("customer");
		        Predicate customerIdPredicate = builder.equal(addressJoin.get("customerId"), orderSearchParameters.getCustomerId());
		        return builder.and(customerIdPredicate);
		    };
		    spec = spec.and(customerIdSpec);
		}

		if (orderSearchParameters.getPoNo() != null) {
		    Specification<Orders> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("PONumber"), orderSearchParameters.getPoNo());
		    spec = spec.and(poNoSpec);
		}
		
		if (orderSearchParameters.getInvoiceNo() != null) {
		    Specification<Orders> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("invoiceNo"), orderSearchParameters.getInvoiceNo());
		    spec = spec.and(poNoSpec);
		}

		List<Orders> poList= orderRepository.findAll(spec);
		List<OrderDTO> poDTOList= new ArrayList<>();
		poList.forEach(purchaseOrder-> {
			poDTOList.add(orderMapper.PurchaseOrderToPurchaseOrderDTO(purchaseOrder));
		});
		return poDTOList;
	}
}
