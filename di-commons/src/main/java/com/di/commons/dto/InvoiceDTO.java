package com.di.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
	
	private String invNo;
	private String InvDate;
	private String SONo;
	private String PONo;
	private String currency;
	private String customerId;
	private String contactId;
	private String contactEmail;
	private String contactPhone;
	private String contactName;
	private String salesLoc;
	private String locId;
	private String quantity;
	private String itemDesc;
	private String partNo;
	private String brand;
	private String amt;
	private String warehouseId;
    private Boolean isActive;

}
