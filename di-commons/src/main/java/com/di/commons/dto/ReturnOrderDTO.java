package com.di.commons.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.Orders;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
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

	//private Long id;
	private Long ORMOrder;
	private String salesLocationId;
	private String companyId;
	private String contactId;
	private String orderNo;
	private String invoiceNo;
	private String PONumber;
	private Date orderDate;
	private Date createdDate;
	private Date updatedDate;
	private Date requestedDate;
	private String status;
	private String currency;
	private String quantity;
	
	private CustomerDTO customer;
	//private OrderDTO order;
	private List<ReturnOrderItemDTO> returnOrderItem;

	private OrderAddressDTO shipTo;
	
	private ContactDTO contact;

	private OrderAddressDTO billTo;
	private String rmaOrderNo;

//	private Orders orders;
}