package com.continuum.repos.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class OrderAddress extends BaseEntity {

	private String phoneNumber;
	private String fax;
	private String street1;
	private String street2;
	private String country;
	private String province;
	private String city;
	private String zipcode;
	private String addressType;

	 
	/*
	 * @JoinColumn(name = "purchaseOrderId") private PurchaseOrder purchaseOrder;
	 */
	
	@ManyToOne
	@JoinColumn(name = "purchaseOrderItemId")
	private PurchaseOrderItem purchaseOrderItem;

	@ManyToOne
	@JoinColumn(name = "returnOrderId")
	private ReturnOrder returnOrder;

	@ManyToOne
	@JoinColumn(name = "returnOrderItemId")
	private ReturnOrderItem returnOrderItem;

}
