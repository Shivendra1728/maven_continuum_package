package com.continuum.tenant.repos.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReturnOrderItem extends BaseEntity {

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "returnOrderId")
	private ReturnOrder returnOrder;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "shipTo")
	private OrderAddress shipTo;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "billTo")
	private OrderAddress billTo;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "returnOrderItemId")
	private List<OrderItemDocuments> orderItemDocuments;

	@OneToMany(mappedBy = "returnOrderItem", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ReturnRoom> returnRooms = new HashSet<ReturnRoom>();

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	@JoinColumn(name = "returnOrderItemId")
	private Set<QuestionMap> questionMap;
	
	
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "returnOrderItemId")
	private Set<SerialData> serialData;

	/*
	 * @OneToOne(mappedBy = "returnOrderItem", cascade = { CascadeType.ALL}) private
	 * ReasonCode reasonCode;
	 */
	/*
	 * @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	 * 
	 * @JoinColumn(name ="purchaseOrderItemId") private OrderItem orderItem;
	 */
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
	private String reasonCode;

	private String trackingUrl;
	private String trackingNumber;
	private String courierName;
	private String note;
	private String Notes;
	private BigDecimal Amount;
	private String amountNote;
	@ManyToOne
	@JoinColumn(name = "assignTo")
	private User user;
	private String itemDesc;

	private Date followUpDate;
	private Long reasonCodeId;
	private String problemDescNote;
	private String vendorMessage;
	private Boolean isEditable;
	private Boolean isAuthorized;
	private Boolean isActive;
	private String deleteNote;
	private String returnLocationId;
	private Boolean isSerialized;
	private String serialNo;
	private Integer invoiceNo;

}