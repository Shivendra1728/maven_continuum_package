package com.di.commons.p21.mapper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.helper.P21InvoiceData;
import com.di.commons.helper.P21OrderData;
import com.di.commons.helper.P21OrderLineItem;
import com.di.commons.helper.P21OrderLineItemHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21OrderLineItemMapper {
	@Autowired
	private final ObjectMapper objectMapper;

	public P21OrderLineItemMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public List<OrderItemDTO> convertP21OrderLineObjectToOrderLineDTO(String item,
			OrderSearchParameters orderSearchParameters, String invoiceNo)
			throws JsonMappingException, JsonProcessingException, ParseException {
		P21OrderLineItemHelper p21OrderLineItemHelper = objectMapper.readValue(item, P21OrderLineItemHelper.class);
		List<OrderItemDTO> orderItemDTOList = new ArrayList<>();
		List<P21OrderLineItem> p21OrderLineItemList = p21OrderLineItemHelper.getValue();
		P21InvoiceData p21InvoiceData = new P21InvoiceData();
		for (P21OrderLineItem p21OrderLineItem : p21OrderLineItemList) {
			OrderItemDTO orderitemDTO = new OrderItemDTO();
			orderitemDTO.setOrderNo(p21OrderLineItem.getOrder_no());
			orderitemDTO.setDescription(p21OrderLineItem.getItem_desc());
			orderitemDTO.setPartNo(p21OrderLineItem.getItem_id());
			orderitemDTO.setAmount(new BigDecimal(p21OrderLineItem.getUnit_price()));
			orderitemDTO.setParentLineId(Long.parseLong(p21OrderLineItem.getParent_oe_line_uid()));
			orderitemDTO.setItemName(p21OrderLineItem.getItem_id());
			orderitemDTO.setOrderLineId(Long.parseLong(p21OrderLineItem.getOe_line_uid()));
			orderitemDTO.setLineNo(p21OrderLineItem.getLine_number());
			orderitemDTO.setInvoiceNo(invoiceNo);
			orderitemDTO.setQuantity(Math.abs((int) Double.parseDouble(p21OrderLineItem.getOrdered_qty())));
			orderitemDTO.setOldQuantity(Math.abs((int) Double.parseDouble(p21OrderLineItem.getOrdered_qty())));
			orderitemDTO.setInvoiceDate(p21OrderLineItem.getOriginal_invoice_date());
			orderitemDTO.setOtherCharge(p21OrderLineItem.getOther_charge()); //Other charge

			// this below code for shipTo and BillTo mapping for particular lineitem.

			P21OrderData p21OrderData = new P21OrderData();
			OrderAddressDTO orderAddressShipTODTO = new OrderAddressDTO();

			// orderAddressShipTODTO.setId(p21OrderData.getid());
			// orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			// orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			// orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No
			// keys mapped here yet
			orderAddressShipTODTO.setAddressId(p21OrderData.getAddress_id()); // TO Do Addrress API
			orderAddressShipTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressShipTODTO.setStreet1(p21OrderData.getShip2_add1());
			orderAddressShipTODTO.setStreet2(p21OrderData.getShip2_add2());
			orderAddressShipTODTO.setCountry(p21OrderData.getShip2_country());
			orderAddressShipTODTO.setProvince(p21OrderData.getShip2_state());
			orderAddressShipTODTO.setCity(p21OrderData.getShip2_city());
			orderAddressShipTODTO.setZipcode(p21OrderData.getShip2_zip());
			// orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderitemDTO.setShipTo(orderAddressShipTODTO);

			OrderAddressDTO orderAddressBillTODTO = new OrderAddressDTO();

			// orderAddressShipTODTO.setId(p21OrderData.getid());
			// orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			// orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			// orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No
			// keys mapped here yet
			// orderAddressBillTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressBillTODTO.setStreet1(p21OrderData.getMail_address1_a());
			orderAddressShipTODTO.setStreet2(p21OrderData.getMail_address2_a());
			orderAddressShipTODTO.setCountry(p21OrderData.getMail_country_a());
			orderAddressShipTODTO.setProvince(p21OrderData.getMail_state_a());
			orderAddressShipTODTO.setCity(p21OrderData.getMail_city_a());
			orderAddressShipTODTO.setZipcode(p21OrderData.getMail_postal_code_a());
			// orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderitemDTO.setBillTo(orderAddressBillTODTO);

			orderItemDTOList.add(orderitemDTO);
		}
		return orderItemDTOList;
	}

	public List<OrderItemDTO> convertP21OrderLineObjectToOrderLineDTOForInvoice(String orderLineDataFromInvoice,
			String invoiceNo) throws JsonMappingException, JsonProcessingException {
		List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(orderLineDataFromInvoice);
		JsonNode valueNode = jsonNode.get("value");

		for (JsonNode itemNode : valueNode) {
			OrderItemDTO orderItemDTO = new OrderItemDTO();

			orderItemDTO.setOrderNo(itemNode.get("order_no").asText());
			orderItemDTO.setDescription(itemNode.get("item_desc").asText());
			orderItemDTO.setPartNo(itemNode.get("item_id").asText());
			orderItemDTO.setAmount(new BigDecimal(itemNode.get("unit_price").asText()));
			orderItemDTO.setItemName(itemNode.get("item_id").asText());
			orderItemDTO.setId(Long.parseLong(itemNode.get("invoice_line_uid").asText()));
			orderItemDTO.setParentLineId(Long.parseLong(itemNode.get("invoice_line_uid_parent").asText()));
			orderItemDTO.setLineNo(itemNode.get("line_no").asText());
			orderItemDTO.setInvoiceNo(invoiceNo);
			orderItemDTO.setQuantity(Math.abs((int) Double.parseDouble(itemNode.get("qty_shipped").asText())));
			orderItemDTO.setInvoiceDate(itemNode.get("date_created").asText());
			orderItemDTO.setOtherCharge(itemNode.get("other_charge_item").asText());
			
			
			
			P21OrderData p21OrderData = new P21OrderData();
			OrderAddressDTO orderAddressShipTODTO = new OrderAddressDTO();

			orderAddressShipTODTO.setAddressId(p21OrderData.getAddress_id());
			orderAddressShipTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressShipTODTO.setStreet1(p21OrderData.getShip2_add1());
			orderAddressShipTODTO.setStreet2(p21OrderData.getShip2_add2());
			orderAddressShipTODTO.setCountry(p21OrderData.getShip2_country());
			orderAddressShipTODTO.setProvince(p21OrderData.getShip2_state());
			orderAddressShipTODTO.setCity(p21OrderData.getShip2_city());
			orderAddressShipTODTO.setZipcode(p21OrderData.getShip2_zip());

			orderItemDTO.setShipTo(orderAddressShipTODTO);

			OrderAddressDTO orderAddressBillTODTO = new OrderAddressDTO();

			orderAddressBillTODTO.setStreet1(p21OrderData.getMail_address1_a());
			orderAddressShipTODTO.setStreet2(p21OrderData.getMail_address2_a());
			orderAddressShipTODTO.setCountry(p21OrderData.getMail_country_a());
			orderAddressShipTODTO.setProvince(p21OrderData.getMail_state_a());
			orderAddressShipTODTO.setCity(p21OrderData.getMail_city_a());
			orderAddressShipTODTO.setZipcode(p21OrderData.getMail_postal_code_a());

			orderItemDTO.setBillTo(orderAddressBillTODTO);

			orderItemDTOList.add(orderItemDTO);
		}

		return orderItemDTOList;
	}

}