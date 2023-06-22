package com.di.commons.p21.mapper;

import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderItemDTO;

import com.di.commons.helper.P21OrderLineItem;

import com.di.commons.helper.P21OrderLineItemHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component

public class P21OrderLineItemMapper {

	@Autowired

	private final ObjectMapper objectMapper;

	public P21OrderLineItemMapper(ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;

	}

	public List<OrderItemDTO> convertP21OrderObjectToOrderDTO(String item) {

		try {

			P21OrderLineItemHelper p21OrderLineItemHelper = objectMapper.readValue(item, P21OrderLineItemHelper.class);

			List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

			List<P21OrderLineItem> p21OrderLineItemList = p21OrderLineItemHelper.getValue();

			for (P21OrderLineItem p21OrderLineItem : p21OrderLineItemList) {

				OrderItemDTO orderitemDTO = new OrderItemDTO();

				// orderitemDTO.setQuantity(p21OrderLineItem.getQty_ordered());

				orderitemDTO.setDescription(p21OrderLineItem.getItem_desc());

				orderitemDTO.setPartNo(p21OrderLineItem.getItem_id());

				orderitemDTO.setItemName(p21OrderLineItem.getItem_id());

				orderItemDTOList.add(orderitemDTO);

			}

			return orderItemDTOList;

		}

		catch (Exception e) {

			e.printStackTrace();

			return null;

		}

	}

}