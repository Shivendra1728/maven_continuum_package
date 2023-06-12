package com.di.commons.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.continuum.repos.entity.OrderItemDocuments;
import com.continuum.repos.entity.ReturnOrderItem;
import com.continuum.repos.repositories.OrderItemDocumentRepository;

@Component
public class OrderItemDocumentsHelper {

	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;

	public OrderItemDocumentsHelper(OrderItemDocumentRepository orderItemDocumentRepository) {
		this.orderItemDocumentRepository = orderItemDocumentRepository;
	}

	public void storeOrderItemDocument(String uploadDirectory, String fileType, ReturnOrderItem returnOrderItemId) {
		OrderItemDocuments orderItemDocuments = new OrderItemDocuments();
		orderItemDocuments.setURL(uploadDirectory);
		orderItemDocuments.setType(fileType);
		orderItemDocuments.setReturnOrderItem(returnOrderItemId);
		orderItemDocumentRepository.save(orderItemDocuments);
	}
	
	
}
