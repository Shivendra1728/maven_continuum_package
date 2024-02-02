package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
	private Long id;
	private OrderAddressDTO shipTo;
	private OrderAddressDTO billTo;
	private int quantity;
	private Date purchaseDate;
	private String status;
	private String itemName;
	private String partNo;
	private String description;
	private BigDecimal amount;
	private String invoiceNo;
	private String orderNo;
	private String invoiceDate;
	private boolean isEligibleForReturn = true;
	private String searchFrom;
	private boolean isSerialized;
	private String lineNo;
	private Long parentLineId;
	private Long orderLineId;
	private List<OrderItemDTO> innerItems;

	// Copy constructor
	public OrderItemDTO(OrderItemDTO orderItemDTO) {
		this.id = orderItemDTO.getId();
		this.shipTo = orderItemDTO.getShipTo();
		this.billTo = orderItemDTO.getBillTo();
		this.quantity = orderItemDTO.getQuantity();
		this.purchaseDate = orderItemDTO.getPurchaseDate();
		this.status = orderItemDTO.getStatus();
		this.itemName = orderItemDTO.getItemName();
		this.partNo = orderItemDTO.getPartNo();
		this.description = orderItemDTO.getDescription();
		this.amount = orderItemDTO.getAmount();
		this.invoiceNo = orderItemDTO.getInvoiceNo();
		this.orderNo = orderItemDTO.getOrderNo();
		this.invoiceDate = orderItemDTO.getInvoiceDate();
		this.isEligibleForReturn = orderItemDTO.isEligibleForReturn();
		this.searchFrom = orderItemDTO.getSearchFrom();
		this.isSerialized = orderItemDTO.isSerialized();
		this.lineNo = orderItemDTO.getLineNo();
		this.parentLineId = orderItemDTO.getParentLineId();
		this.orderLineId = orderItemDTO.getOrderLineId();

		
	}

	
}
