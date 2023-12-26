package com.continuum.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnOrderItemService;
import com.continuum.serviceImpl.ReturnOrderItemServiceImpl;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.StatusConfig;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.integration.p21.service.P21SKUService;
import com.di.integration.p21.serviceImpl.P21SKUServiceImpl;

@RestController
@RequestMapping("/return_order_items")
public class ReturnOrderItemController {
	final Logger logger = LoggerFactory.getLogger(ReturnOrderItemServiceImpl.class);

//	private final ReturnOrderItemService returnOrderItemService;
//
//	@Autowired
//	public ReturnOrderItemController(ReturnOrderItemService returnOrderItemService) {
//		this.returnOrderItemService = returnOrderItemService;
//  }

	@Autowired
	ReturnOrderItemService returnOrderItemService;

	@Autowired
	P21SKUServiceImpl p21SKUServiceImpl;
	
	@Autowired
	P21SKUService p21skuService;

	@PutMapping("/updatestatus")
	public String updateReturnOrderItem(@RequestParam Long id, @RequestParam String rmaNo,
			@RequestParam String updateBy, @RequestBody ReturnOrderItemDTO updatedItem) {
		return returnOrderItemService.updateReturnOrderItem(id, rmaNo, updateBy, updatedItem);
	}

	@PutMapping("/update/note")
	public String updateNote(@RequestParam long lineItemId, @RequestParam Long assignToId, @RequestParam String rmaNo,
			@RequestParam String updateBy, @RequestParam Long assignToRole, @RequestParam String contactEmail,
			@RequestBody ReturnOrderItemDTO updatedNote) {
		return returnOrderItemService.updateNote(lineItemId, assignToId, rmaNo, updateBy, assignToRole, contactEmail,
				updatedNote);
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

	@DeleteMapping("/deleteItem")
	public String deleteItem(@RequestBody ReturnOrderItem returnOrderItem, @RequestParam String updateBy,
			@RequestParam String rmaNo) throws Exception {

		String response = p21SKUServiceImpl.deleteSKU(returnOrderItem.getItemName(), rmaNo, null);
		logger.info("This is response from ERP Deletion method :: " + response);
		if ("Item Deleted".equals(response) || "Process Complete Line Item from ERP deleted".equals(response)) {
			return returnOrderItemService.deleteItem(returnOrderItem, updateBy, rmaNo);
		} else {
			return "ERP deletion not allowed for this line item.";
		}

	}
	
	
	@PostMapping("/add")
	public String addItem(@RequestBody List<ReturnOrderItemDTO> returnOrderItemDTOList , @RequestParam String updateBy , @RequestParam String rmaNo) throws Exception{
		String response = p21skuService.addSKU(rmaNo, returnOrderItemDTOList, null);
		if(response.equalsIgnoreCase("Line Item Added")) {
		return returnOrderItemService.addItem(returnOrderItemDTOList,updateBy,rmaNo);
		}else {
			return "ERP addition not allowed for this line item.";
		}
		
	}
	
}