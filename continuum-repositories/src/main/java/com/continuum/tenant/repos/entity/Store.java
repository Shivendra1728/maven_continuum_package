package com.continuum.tenant.repos.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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

	 @Column(name = "store_name")
    private String storeName;
	
	  private String StoreCode; private String description; private String URL;
	  
	  @Builder.Default private boolean enabled= true;
	  
	  private String storeType; private String contentEncoding; private Integer
	  noOfStores;
	  
	  
	  
	  @Builder.Default
	  
	  private boolean storeState = true; private String feeType;
	  
	  private String reasonListing; private String category;
	 

    @OneToOne
    @JoinColumn(name="storeLocaleId")
    private StoreLocale storeLocale;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private StoreAddress shipTo;

}