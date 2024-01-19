package com.continuum.tenant.repos.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class OrderItem extends BaseEntity {

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "orderId")
	private Orders orders;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "shipTo")
	private OrderAddress shipTo;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "billTo")
	private OrderAddress billTo;;

	private int quantity;
	private Date purchaseDate;
	private String status;
	private String itemName;
	private String description;
	private BigDecimal amount;

}
