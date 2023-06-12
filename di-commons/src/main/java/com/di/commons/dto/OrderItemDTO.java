package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
	    private Long id;
	 	private OrderAddressDTO shipTo;
	    private OrderAddressDTO billTo;
		private int quantity;
		private Date purchaseDate;
		private String status;
		private String itemName;
		private String partNo;
		private String description;
		private BigDecimal amount;
}