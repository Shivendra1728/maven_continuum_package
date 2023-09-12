package com.continuum.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnOrderItemService;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.di.commons.dto.ReturnOrderItemDTO;

@RestController
@RequestMapping("/return_order_items")
public class ReturnOrderItemController {

	private final ReturnOrderItemService returnOrderItemService;

	@Autowired
	public ReturnOrderItemController(ReturnOrderItemService returnOrderItemService) {
		this.returnOrderItemService = returnOrderItemService;
	}

	@PutMapping("/updatestatus")
	public String updateReturnOrderItem(@RequestParam Long id, @RequestBody ReturnOrderItemDTO updatedItem) {
		return returnOrderItemService.updateReturnOrderItem(id, updatedItem);
	}

	@PutMapping("/update/note")
	public String updateNote(@RequestParam long lineItemId, @RequestParam Long assignToId,
			@RequestBody ReturnOrderItemDTO updatedNote) {
		return returnOrderItemService.updateNote(lineItemId, assignToId, updatedNote);
	}

	@PutMapping("/UpdateShipTo")
	public String UpdateShipTo(@RequestParam Long RtnOrdId, @RequestBody OrderAddress orderAddress) {
		return returnOrderItemService.updateShipTo(RtnOrdId, orderAddress);

	}
	
	@PutMapping("/update/restocking")
	public String updateRestockingFee(@RequestParam Long Id, @RequestParam BigDecimal reStockingAmount,@RequestBody ReturnOrderItemDTO returnOrderItemDTO) {
		return returnOrderItemService.updateRestockingFee(Id,reStockingAmount,returnOrderItemDTO);

	}

}
