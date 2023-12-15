package com.di.integration.p21.service;

import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.di.commons.helper.OrderSearchParameters;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface P21UpdateRMAService {
	String updateRMARestocking(String rmaNumber, Double totalRestocking) throws Exception;
	String updateAmount(String rmaNo, List<ReturnOrderItem> returnOrderItems) throws Exception;
}
