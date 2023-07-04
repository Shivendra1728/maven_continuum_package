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
@Component
public class ReturnOrderDTO {

	private String id;
	private Long ORMOrder;
	private String salesLocationId;
	private String companyId;
	private String contactId;
	private String orderNo;
	private String invoiceNo;
	private String PONumber;
	private Date orderDate;
	private Date requestedDate;
	private String status;
	private String currency;
	private String quantity;
	
	
	private CustomerDTO customer;
	//private OrderDTO order;
	private List<ReturnOrderItemDTO> returnOrderItem;

	private OrderAddressDTO shipTo;

	private OrderAddressDTO billTo;


//	private Orders orders;
}