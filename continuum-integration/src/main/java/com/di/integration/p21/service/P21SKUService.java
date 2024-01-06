package com.di.integration.p21.service;

import java.util.List;
import java.util.Map;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.ReturnOrderItemDTO;

public interface P21SKUService {

	String deleteSKU(String itemId, String rmaNo, MasterTenant masterTenant) throws Exception;

	public String addSKU(String rmaNo, List<ReturnOrderItemDTO> returnOrderItemDTO, MasterTenant masterTenant)
			throws Exception;

	Map<String, Object> checkSerialized(String itemId, MasterTenant masterTenant) throws Exception;
	public Map<String, Object> isSellable(String itemId,MasterTenant masterTenantObject) throws Exception;

	OrderDTO getProductByProductId(String productId) throws Exception;

}
