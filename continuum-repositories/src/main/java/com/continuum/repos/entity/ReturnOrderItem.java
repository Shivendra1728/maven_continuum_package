package com.continuum.repos.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
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

	@ManyToOne
	@JoinColumn(name="returnOrderItem")
	private ReturnOrder returnOrder;
	
	@ManyToOne
	@JoinColumn(name="shippTo")
    private OrderAddress shippTo;
	
	@ManyToOne
	@JoinColumn(name="billTo")
    private OrderAddress billTo;
	
	@OneToMany(mappedBy = "returnOrderItem")
    private List<OrderItemDocuments> orderItemDocuments;
	
	@OneToOne(mappedBy = "returnOrderItem")
    private ReasonCode reasonCode;
	
	private String returnComments;
	private int quanity;
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
	
	
	
	
	
}
