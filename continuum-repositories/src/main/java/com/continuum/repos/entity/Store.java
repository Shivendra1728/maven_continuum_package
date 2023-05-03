package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.Entity;
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
	private boolean enabled;
	private String defaultLocale;
	private String country;
	private String subCountry;
	private String timeZone;
	private String storeType;
	private String contentEncoding;
	private boolean storeState;
	private String storeConfigURL;
	
	@OneToOne
	@JoinColumn(name="storeLocaleId")
	private StoreLocale storeLocale; 
	
	@OneToMany(mappedBy = "store")
	private List<Customer> customers;
}
