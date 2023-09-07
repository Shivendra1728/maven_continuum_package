package com.continuum.controller;

import com.di.commons.dto.ReturnOrderItemDTO;
import com.continuum.service.ReturnOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/return_order_items")
public class ReturnOrderItemController {

    private final ReturnOrderItemService returnOrderItemService;

    @Autowired
    public ReturnOrderItemController(ReturnOrderItemService returnOrderItemService) {
        this.returnOrderItemService = returnOrderItemService;
    }

    @PutMapping("/{id}")
    public String updateReturnOrderItem(@PathVariable Long id, @RequestBody ReturnOrderItemDTO updatedItem) {
        return returnOrderItemService.updateReturnOrderItem(id, updatedItem);
    }
}

