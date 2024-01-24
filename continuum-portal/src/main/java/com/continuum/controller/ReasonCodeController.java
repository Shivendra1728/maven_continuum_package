package com.continuum.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReasonCodeService;
import com.di.commons.dto.ReasonCodeDTO;

@RestController
@RequestMapping("/reasoncodes")
public class ReasonCodeController {

	private final ReasonCodeService reasonCodeService;

	public ReasonCodeController(ReasonCodeService reasonCodeService) {
		this.reasonCodeService = reasonCodeService;
	}

	@GetMapping("/searchbyStore")
	public ResponseEntity<List<ReasonCodeDTO>> searchReasonFlatCodesByStoreId(@RequestParam("storeId") Long storeId) {
		List<ReasonCodeDTO> reasonCodes = reasonCodeService.searchReasonFlatCodesByStoreId(storeId);
		return ResponseEntity.ok(reasonCodes);
	}

	@PostMapping("/create/v1")
	public String createOrder(@RequestBody ReasonCodeDTO reasonCodeDTO) {
		return reasonCodeService.createOrder(reasonCodeDTO);
	}
}