package com.di.integration.p21.service;

import java.math.BigDecimal;

import com.di.commons.helper.OrderSearchParameters;

public interface P21UpdateRMAService {
	String updateRMARestocking(Integer rmaNumber, Integer poNumber, Double totalRestocking) throws Exception;
}
