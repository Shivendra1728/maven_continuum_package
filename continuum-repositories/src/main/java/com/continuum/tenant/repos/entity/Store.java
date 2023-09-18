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
	 
	 @Column(name = "store_code")
	  private String StoreCode; 
	  private String description; 
	  private String URL;
	  
	  @Builder.Default 
	  private boolean enabled= true;
	  @Column(name = "store_type")
	  private String storeType; 
	  private String contentEncoding; 
	  private Integer noOfStores;
	  
	  
	  
	  @Column(name="store_state")
	  private boolean storeState;
	  private String feeType;
	  
	  private String reasonListing; 
	  private String category;
	 

    @OneToOne
    @JoinColumn(name="storeLocaleId")
    private StoreLocale storeLocale;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private StoreAddress shipTo;

}