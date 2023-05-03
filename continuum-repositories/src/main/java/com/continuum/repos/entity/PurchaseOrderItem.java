package com.continuum.repos.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
public class PurchaseOrderItem extends BaseEntity {

	@ManyToOne
	@JoinColumn(name="purchaseOrderId")
	private PurchaseOrder purchaseOrder;
	
	/*
	 * @OneToMany(mappedBy = "purchaseOrderItem") private List<OrderAddress> billTo;
	 */
	
	@ManyToOne
	@JoinColumn(name="shippTo")
    private OrderAddress shippTo;
	
	@ManyToOne
	@JoinColumn(name="billTo")
    private OrderAddress billTo;
	
	
}
