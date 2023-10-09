package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
