package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnOrderItemDTO {
	
	private Long id;
    private OrderAddressDTO shippTo;
    private OrderAddressDTO billTo;
    private List<OrderItemDocumentsDTO> orderItemDocuments;
    private ReasonCodeDTO reasonCode;
	private String returnComments;
	private int quanity;
	private String itemName;
	private BigDecimal returnAmount;
	private BigDecimal reStockingAmount;
	private BigDecimal shippingCost;
	private BigDecimal shippingTax;
	private Date purchaseDate;
	private Date incidentDate;
	private String problemDesc;
	private int receivedQuantity;
	private String receivedState;
	private String status;
	private PurchaseOrderItemDTO purchaseOrderItemDTO;

}
