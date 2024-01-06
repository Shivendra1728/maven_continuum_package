package com.di.integration.p21.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.integration.p21.service.P21SKUService;

@RestController
@RequestMapping("/P21/SKU")
public class P21SKUController {

	@Autowired
	P21SKUService p21SKUService;

	@GetMapping("/search")
	public OrderDTO getProductByProductId(@RequestParam(required = true) String productId) throws Exception {
		return p21SKUService.getProductByProductId(productId);

	}

	@DeleteMapping("/delete")
	public String skuDelete(@RequestParam(required = true) String itemId, @RequestParam(required = true) String rmaNo)
			throws URISyntaxException, Exception {
		return p21SKUService.deleteSKU(itemId, rmaNo, null);
	}

	@PostMapping("/add")
	public String skuAdd(@RequestParam(required = true) String rmaNo,
			@RequestBody List<ReturnOrderItemDTO> returnOrderItemDTOList) throws URISyntaxException, Exception {
		return p21SKUService.addSKU(rmaNo, returnOrderItemDTOList, null);
	}

	@GetMapping("/isSerialized")
	public ResponseEntity<Map<String, Object>> checkSerialized(@RequestParam String itemId) throws Exception {
		Map<String, Object> response = p21SKUService.checkSerialized(itemId, null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/isSellable")
	public Map<String, Object> isSellable(@RequestParam(required = true) String itemId) throws Exception {
		return p21SKUService.isSellable(itemId, null);
	}

}
