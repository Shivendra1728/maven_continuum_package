package com.continuum.service;

import java.util.List;

import com.continuum.repos.entity.ReasonCode;
import com.di.commons.dto.ReasonCodeDTO;

public interface ReasonCodeService {

	//public List<ReasonCodeDTO> searchReasonCodesByStoreId(long storeId);
	public List<ReasonCode> searchReasonCodesByStoreId(Long storeId);
		

}
