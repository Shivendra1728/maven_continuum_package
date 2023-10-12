package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.continuum.tenant.repos.entity.BaseEntity;
import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@ToString
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)

public class ReturnOrderItemDTO extends BaseEntity{

	private Long id;
	private OrderAddressDTO shipTo;
	private OrderAddressDTO billTo;
	private List<OrderItemDocumentsDTO> orderItemDocuments;
    private List<ReturnRoomDTO> returnRooms;
    
	private String reasonCode;
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

	private String trackingUrl;
	private Long trackingNumber;
	private String courierName;
	private String note;
	private String Notes;
	private BigDecimal Amount;

	@ManyToOne
	@JoinColumn(name = "assignTo")
	private User user;

	private Date followUpDate;
	private String itemDesc;
	
	@OneToMany
	@JoinColumn(name = "returnOrderItemId")
	Set<QuestionMap> questionMap;
	
	private Long reasonCodeId;
	private String returnType;

}