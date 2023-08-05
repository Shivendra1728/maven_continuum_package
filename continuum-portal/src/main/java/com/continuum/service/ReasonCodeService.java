package com.continuum.service;
import java.util.List;

import com.continuum.tenant.repos.entity.ReasonCode;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReasonCodeDTO;
import com.di.commons.helper.OrderSearchParameters;

public interface ReasonCodeService {

	// public List<ReasonCodeDTO> searchReasonCodesByStoreId(long storeId);
	public List<ReasonCode> searchReasonCodesByStoreId(Long storeId);
	public List<ReasonCodeDTO> searchReasonFlatCodesByStoreId(Long storeId);
	public String createOrder(ReasonCodeDTO reasonCodeDTO);

}