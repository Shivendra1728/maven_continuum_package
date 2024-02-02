package com.continuum.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.StatusConfig;
import com.di.commons.dto.ReturnOrderItemDTO;

public interface ReturnOrderItemService {
	String updateReturnOrderItem(Long id, String rmaNo,String updateBy, ReturnOrderItemDTO updatedItem);

	String updateNote(Long lineItemId, Long assignToId, String rmaNo,String updateBy,Long assignToRole,  String contactEmail,ReturnOrderItemDTO updateNote);

	String updateShipTo(Long rtnOrdId, String rmaNo,String updateByName, OrderAddress orderAddress);

	String updateRestockingFee(Long id, String rmaNo,String updateBy, BigDecimal reStockingAmount,
			ReturnOrderItemDTO returnOrderItemDTO);

	List<StatusConfig> getAllStatus();

	List<QuestionConfig> getQuestions();
	
	Map<String, Object> deleteItem(ReturnOrderItem returnOrderItem , String updateBy , String rmaNo) throws Exception;
	Map<String, Object> addItem(List<ReturnOrderItemDTO> returnOrderItemList , String updateBy , String rmaNo) throws Exception;
	
	String deleteAttachment(Long deleteAttachment);

	
}
