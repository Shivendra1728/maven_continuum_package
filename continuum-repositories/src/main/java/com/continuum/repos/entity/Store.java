package com.continuum.repos.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
public class Store extends BaseEntity{
	
	private String storeName;
	private String StoreCode;
	private String description;
	private String URL;
	
	@Builder.Default
	private boolean enabled= true;
	@Builder.Default
	private boolean forceAccepTAndC= true;
	private String ERPConnectionString;
	
	@Builder.Default
	private boolean notificationEnable= true;
	
	@Builder.Default
	private boolean seperateDBInstance= true;
	
	private String filterSearchConfiguration;
	
	private String ERPDataSychInterval;
	
	private String defaultLocale;
	//private String country;
	//private String subCountry;
	private String timeZone;
	private String storeType;
	private String contentEncoding;
	private Integer returnPolicyPeriod;
	
	private Integer noOfStores;
	
	@Builder.Default
	private boolean storeState = true;
	
	@OneToOne
	@JoinColumn(name="storeLocaleId")
	private StoreLocale storeLocale; 
	
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private StoreAddress shipTo;
}
