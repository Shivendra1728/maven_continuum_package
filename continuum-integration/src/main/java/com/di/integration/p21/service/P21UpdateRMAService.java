package com.di.integration.p21.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.di.integration.p21.transaction.ReturnLocation;

public interface P21UpdateRMAService {
	String updateRMARestocking(String rmaNumber, Double totalRestocking) throws Exception;
	String updateAmount(String rmaNo, ReturnOrderItem returnOrderItem) throws Exception;
	ResponseEntity<String> updateItemReturnLocation(String rmaNo, String itemId, String returLocationId) throws Exception;
	List<ReturnLocation> getReturnLocations(String itemId) throws Exception;
}
