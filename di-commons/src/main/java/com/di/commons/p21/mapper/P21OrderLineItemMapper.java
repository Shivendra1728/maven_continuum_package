package com.di.commons.p21.mapper;

import java.math.BigDecimal;

import java.util.ArrayList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.List;

import java.util.Date;

import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderItemDTO;

import com.di.commons.helper.P21OrderLineItem;

import com.di.commons.helper.P21OrderLineItemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component

public class P21OrderLineItemMapper {

	@Autowired

	private final ObjectMapper objectMapper;

	public P21OrderLineItemMapper(ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;

	}

	public List<OrderItemDTO> convertP21OrderLineObjectToOrderLineDTO(String item)
			throws JsonMappingException, JsonProcessingException, ParseException {

		P21OrderLineItemHelper p21OrderLineItemHelper = objectMapper.readValue(item, P21OrderLineItemHelper.class);

		List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

		List<P21OrderLineItem> p21OrderLineItemList = p21OrderLineItemHelper.getValue();

		for (P21OrderLineItem p21OrderLineItem : p21OrderLineItemList) {

			OrderItemDTO orderitemDTO = new OrderItemDTO();

			// orderitemDTO.setQuantity(p21OrderLineItem.getQty_ordered());

			String invoiceDate = p21OrderLineItem.getOriginal_invoice_no();

			if (invoiceDate != null) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

				Date parsedInvoiceDate = dateFormat.parse(invoiceDate);

				orderitemDTO.setPurchaseDate(parsedInvoiceDate);

			} else {

				orderitemDTO.setPurchaseDate(null);

			}

			orderitemDTO.setDescription(p21OrderLineItem.getItem_desc());
			orderitemDTO.setPartNo(p21OrderLineItem.getItem_id());

			orderitemDTO.setAmount(new BigDecimal(p21OrderLineItem.getUnit_price()));

			orderitemDTO.setItemName(p21OrderLineItem.getItem_id());
			orderitemDTO.setInvoiceNo(p21OrderLineItem.getOriginal_invoice_no());
			BigDecimal decimalQuantity = new BigDecimal(p21OrderLineItem.getOrdered_qty());

			int quantity = decimalQuantity.setScale(0, RoundingMode.HALF_UP).intValue();

			orderitemDTO.setQuantity(quantity);

			orderItemDTOList.add(orderitemDTO);

		}

		return orderItemDTOList;

	}

}