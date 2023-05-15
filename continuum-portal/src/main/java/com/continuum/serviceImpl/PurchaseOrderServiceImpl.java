package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.OrderAddress;
import com.continuum.repos.entity.PurchaseOrder;
import com.continuum.repos.repositories.PurchaseOrderRepository;
import com.continuum.service.PurchaseOrderService;
import com.di.commons.dto.PurchaseOrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.mapper.PurchaseOrderMapper;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
	
	@Autowired
	PurchaseOrderRepository orderRepository;
	
	@Autowired
	PurchaseOrderMapper purchaseOrderMapper;
	

	@Override
	public String createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
		PurchaseOrder purchaseOrder= purchaseOrderMapper.PurchaseOrderDTOToPurchaseOrder(purchaseOrderDTO);
		orderRepository.save(purchaseOrder);
		return "PO created succssfully";
	}
	
	@Override
	public List<PurchaseOrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) {
		
		Specification<PurchaseOrder> spec = Specification.where(null);

		if (orderSearchParameters.getZipcode() != null) {
		    Specification<PurchaseOrder> zipcodeSpec = (root, query, builder) -> {
		        Join<PurchaseOrder, OrderAddress> addressJoin = root.join("billTo");
		        Predicate zipcodePredicate = builder.equal(addressJoin.get("zipcode"), orderSearchParameters.getZipcode());
		        return builder.and(zipcodePredicate);
		    };
		    spec = spec.and(zipcodeSpec);
		}
		if (orderSearchParameters.getCustomerId() != null) {
		    Specification<PurchaseOrder> customerIdSpec = (root, query, builder) -> {
		        Join<PurchaseOrder, OrderAddress> addressJoin = root.join("customer");
		        Predicate customerIdPredicate = builder.equal(addressJoin.get("customerId"), orderSearchParameters.getCustomerId());
		        return builder.and(customerIdPredicate);
		    };
		    spec = spec.and(customerIdSpec);
		}

		if (orderSearchParameters.getPoNo() != null) {
		    Specification<PurchaseOrder> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("PONumber"), orderSearchParameters.getPoNo());
		    spec = spec.and(poNoSpec);
		}
		
		if (orderSearchParameters.getInvoiceNo() != null) {
		    Specification<PurchaseOrder> poNoSpec = (root, query, builder) ->
		        builder.equal(root.get("invoiceNo"), orderSearchParameters.getInvoiceNo());
		    spec = spec.and(poNoSpec);
		}

		List<PurchaseOrder> poList= orderRepository.findAll(spec);
		List<PurchaseOrderDTO> poDTOList= new ArrayList<>();
		poList.forEach(purchaseOrder-> {
			poDTOList.add(purchaseOrderMapper.PurchaseOrderToPurchaseOrderDTO(purchaseOrder));
		});
		return poDTOList;
	}
}
