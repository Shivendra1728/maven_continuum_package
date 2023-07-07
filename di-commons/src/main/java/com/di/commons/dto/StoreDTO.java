package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.List;

import com.continuum.repos.entity.StoreAddress;

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
public class StoreDTO {
	
	private Long id;
	private String storeName;
	private String StoreCode;
	private String description;
	private String URL;
	
	
	private boolean enabled;
	private boolean forceAccepTAndC;
	private String ERPConnectionString;
	
	private boolean notificationEnable;
	
	//@Builder.Default
	private boolean seperateDBInstance;
	
	private String filterSearchConfiguration;
	
	private String ERPDataSychInterval;
	private String feeType;
	
	
	private boolean allow_rstck_fees;
	private BigDecimal reStockingAmount;
	
	private String reasonListing;
	private String category;
	private String defaultLocale;
	//private String country;
	//private String subCountry;
	private String timeZone;
	private String storeType;
	private String contentEncoding;
	private Integer returnPolicyPeriod;
	
	private Integer noOfStores;
	
	private boolean storeState ;
	//private String storeConfigURL;
	
	
	//private StoreLocale storeLocale; 
	
	private List<CustomerDTO> customers;
	

    private StoreAddress shipTo;
    
    //private StoreAddressDTO billTo;
}
