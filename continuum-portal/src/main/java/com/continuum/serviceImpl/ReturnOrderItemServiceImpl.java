package com.continuum.serviceImpl;

import com.continuum.service.ReturnOrderItemService;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.continuum.tenant.repos.entity.*;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.UserRepository;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReturnOrderItemServiceImpl implements ReturnOrderItemService {
	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;

    @Override
    public String updateReturnOrderItem(Long id, ReturnOrderItemDTO updatedItem) {
    	 Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(id);
    	 
    	 if (optionalItem.isPresent()) {
             ReturnOrderItem existingItem = optionalItem.get();

             
             existingItem.setStatus(updatedItem.getStatus());
             existingItem.setProblemDesc(updatedItem.getProblemDesc());
             existingItem.setReasonCode(updatedItem.getReasonCode());
	             returnOrderItemRepository.save(existingItem);
	         return "List Item Details Updated Successfully.";
    	 } else {
             
             throw new EntityNotFoundException("ReturnOrderItem with ID " + id + " not found");
         }
        
        
    }
}
