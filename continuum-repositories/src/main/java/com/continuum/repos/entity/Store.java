package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
	private String defaultLocale;
	private String country;
	private String subCountry;
	private String timeZone;
	private String storeType;
	private String contentEncoding;
	
	@Builder.Default
	private boolean storeState = true;
	private String storeConfigURL;
	
	@OneToOne
	@JoinColumn(name="storeLocaleId")
	private StoreLocale storeLocale; 
	
	@OneToMany(mappedBy = "store")
	private List<Customer> customers;
	
	/*
	 * @OneToMany( fetch = FetchType.LAZY,cascade = CascadeType.ALL)
	 * 
	 * @JoinColumn(name="store") private List<ReasonCode> reasonCodes;
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private StoreAddress shipTo;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name ="billto")
    private StoreAddress billTo;
}
