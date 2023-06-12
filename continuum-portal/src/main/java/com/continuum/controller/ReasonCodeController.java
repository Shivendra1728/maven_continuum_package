package com.continuum.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.repos.entity.ReasonCode;
import com.continuum.service.ReasonCodeService;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReasonCodeDTO;
import com.di.commons.helper.OrderSearchParameters;

@RestController
@RequestMapping("/reasoncodes")
public class ReasonCodeController {

    private final ReasonCodeService reasonCodeService;

    public ReasonCodeController(ReasonCodeService reasonCodeService) {
        this.reasonCodeService = reasonCodeService;
    }


	/*
	 * @PostMapping public ResponseEntity<ReasonCode> createReasonCode(@RequestBody
	 * ReasonCode reasonCode) { ReasonCode createdReasonCode =
	 * reasonCodeService.createReasonCode(reasonCode); return
	 * ResponseEntity.status(HttpStatus.CREATED).body(createdReasonCode); }
	 */
    
   // @GetMapping("/v1/searchbyStore")
    public ResponseEntity<List<ReasonCode>> searchReasonCodesByStoreId(@RequestParam("storeId") long storeId) {
        List<ReasonCode> reasonCodes = reasonCodeService.searchReasonCodesByStoreId(storeId);
        return ResponseEntity.ok(reasonCodes);
    }
    
    @GetMapping("/searchbyStore")
    public ResponseEntity<List<ReasonCodeDTO>> searchReasonFlatCodesByStoreId(@RequestParam("storeId") Long storeId) {
        List<ReasonCodeDTO> reasonCodes = reasonCodeService.searchReasonFlatCodesByStoreId(storeId);
        return ResponseEntity.ok(reasonCodes);
    }
    
    
}

