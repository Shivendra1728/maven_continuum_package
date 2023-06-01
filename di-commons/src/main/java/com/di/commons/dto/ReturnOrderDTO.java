package com.di.commons.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.continuum.repos.entity.Customer;
import com.continuum.repos.entity.OrderAddress;
import com.continuum.repos.entity.Orders;
import com.continuum.repos.entity.ReturnOrderItem;
import com.continuum.repos.entity.User;
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
public class ReturnOrderDTO {

	private Long id;
	private Long ORMOrder;
	private Long salesLocationId;
	private Long contactId;
	private String PONumber;
	private Date orderDate;
	private Date requestedDate;
	private String status;
	private String currency;
	
	private CustomerDTO customer;

	private List<ReturnOrderItemDTO> returnOrderItem;

	private OrderAddressDTO shipTo;

	private OrderAddressDTO billTo;

//	private Orders orders;
}