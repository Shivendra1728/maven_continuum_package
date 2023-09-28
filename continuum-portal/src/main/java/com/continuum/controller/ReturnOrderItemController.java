package com.continuum.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnOrderItemService;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.StatusConfig;
import com.di.commons.dto.ReturnOrderItemDTO;

@RestController
@RequestMapping("/return_order_items")
public class ReturnOrderItemController {

//	private final ReturnOrderItemService returnOrderItemService;
//
//	@Autowired
//	public ReturnOrderItemController(ReturnOrderItemService returnOrderItemService) {
//		this.returnOrderItemService = returnOrderItemService;
//  }
	
	@Autowired
	ReturnOrderItemService returnOrderItemService;

	@PutMapping("/updatestatus")
	public String updateReturnOrderItem(@RequestParam Long id, @RequestParam String rmaNo,
			@RequestParam String updateBy, @RequestBody ReturnOrderItemDTO updatedItem) {
		return returnOrderItemService.updateReturnOrderItem(id, rmaNo, updateBy, updatedItem);
	}

	@PutMapping("/update/note")
	public String updateNote(@RequestParam long lineItemId, @RequestParam Long assignToId, @RequestParam String rmaNo,
			@RequestParam String updateBy, @RequestBody ReturnOrderItemDTO updatedNote) {
		return returnOrderItemService.updateNote(lineItemId, assignToId, rmaNo, updateBy, updatedNote);
	}

	@PutMapping("/UpdateShipTo")
	public String UpdateShipTo(@RequestParam Long RtnOrdId, @RequestParam String rmaNo, @RequestParam String updateBy,
			@RequestBody OrderAddress orderAddress) {
		return returnOrderItemService.updateShipTo(RtnOrdId, rmaNo, updateBy, orderAddress);

	}

	@PutMapping("/update/restocking")
	public String updateRestockingFee(@RequestParam Long Id, @RequestParam String rmaNo, @RequestParam String updateBy,
			@RequestParam BigDecimal reStockingAmount, @RequestBody ReturnOrderItemDTO returnOrderItemDTO) {
		return returnOrderItemService.updateRestockingFee(Id, rmaNo, updateBy, reStockingAmount, returnOrderItemDTO);

	}

	@GetMapping("getAllStatus")
	public List<StatusConfig> getAllStatus() {
		return returnOrderItemService.getAllStatus();

	}

	@GetMapping("/getQuestions")
	public List<QuestionConfig> getQuestions() {
		return returnOrderItemService.getQuestions();
	}

}
