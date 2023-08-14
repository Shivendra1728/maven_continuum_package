package com.continuum.tenant.repos.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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

	@ManyToOne( cascade = CascadeType.ALL)
	@JoinColumn(name="returnOrderId")
	private ReturnOrder returnOrder;
	
	 @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="shipTo")
    private OrderAddress shipTo;
	
	 @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	@JoinColumn(name="billTo")
    private OrderAddress billTo;
	
	@OneToMany(mappedBy = "returnOrderItem", fetch = FetchType.EAGER, cascade = { CascadeType.ALL})
    private List<OrderItemDocuments> orderItemDocuments;
	
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
	
	
	
	
	
}