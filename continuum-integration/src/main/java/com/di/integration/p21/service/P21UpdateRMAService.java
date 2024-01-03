package com.di.integration.p21.service;

import com.continuum.tenant.repos.entity.ReturnOrderItem;

public interface P21UpdateRMAService {
	String updateRMARestocking(String rmaNumber, Double totalRestocking) throws Exception;
	String updateAmount(String rmaNo, ReturnOrderItem returnOrderItem) throws Exception;
}
