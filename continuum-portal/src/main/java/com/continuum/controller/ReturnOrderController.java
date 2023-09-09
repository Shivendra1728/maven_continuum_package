package com.continuum.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnOrderService;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.User;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.transaction.P21RMAResponse;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {

	@Autowired
	ReturnOrderService returnOrderService;

	@PostMapping("/create/v1")
	public P21RMAResponse createReturnOrder(@RequestBody ReturnOrderDTO returnOrderDTO) throws Exception {

		P21RMAResponse rmaResponse = returnOrderService.createReturnOrder(returnOrderDTO);
		returnOrderService.crateReturnOrderInDB(returnOrderDTO, rmaResponse);
		return rmaResponse;

	}

	@GetMapping("/search")
	public List<ReturnOrderDTO> getReturnOrdersBySearchCriteria(@RequestParam(required = false) String zipcode,
			@RequestParam(required = false) String poNo, @RequestParam(required = false) String customerId,
			@RequestParam(required = false) String invoiceNo) {
		OrderSearchParameters orderSearchParameters = new OrderSearchParameters();
		orderSearchParameters.setZipcode(zipcode);
		orderSearchParameters.setPoNo(poNo);
		orderSearchParameters.setCustomerId(customerId);
		orderSearchParameters.setInvoiceNo(invoiceNo);
		return returnOrderService.getReturnOrdersBySearchCriteria(orderSearchParameters);
	}

	@GetMapping("/getReturnOrder")
	public List<ReturnOrderDTO> getAllReturnOrder() {
		return returnOrderService.getAllReturnOrder();
	}

	@GetMapping("/getByrmaorderNo")
	public List<ReturnOrderDTO> getAllReturnOrderByRmaNo(@RequestParam String rmaOrderNo) throws Exception {
		return returnOrderService.getAllReturnOrderByRmaNo(rmaOrderNo);
	}
	
	@PutMapping("/updateRmaStatus")
	public String updateReturnOrder(@RequestParam Long id, @RequestBody Map<String, String> requestBody) {
		String status = requestBody.get("status");
		return returnOrderService.updateReturnOrder(id,status);
	}
}
