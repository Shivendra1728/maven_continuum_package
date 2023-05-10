package com.di.commons.dto;

import java.util.Date;
import java.util.List;

import com.continuum.repos.entity.PurchaseOrder;
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
	    private Long userId;
	    private Long customerId;
	    private Long salesLocationId;
	    private OrderAddressDTO shipTo;
	    private OrderAddressDTO billTo;
	    private Long contactId;
	    private String PONumber;
	    private Date orderDate;
	    private Date requestedDate;
	    private String status;
	    private String currency;
		private Date createdDate;
		private Date updatedDate;
		private List<ReturnOrderItemDTO> returnOrderItemDTOList;
		private PurchaseOrderDTO purchaseOrderDTO;
}
