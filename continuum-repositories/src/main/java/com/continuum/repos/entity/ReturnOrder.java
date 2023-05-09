package com.continuum.repos.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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

/*
 * @Getter
 * 
 * @Setter
 * 
 * @NoArgsConstructor
 * 
 * @Accessors(chain = true)
 * 
 * @Document(collection = "trip")
 */
public class ReturnOrder  extends BaseEntity{
	
	  
	    private Long ORMOrder;
	    private Long salesLocationId;
	    private Long shipTo;
	    private Long billto;
	    private Long contactId;
	    private String PONumber;
	    private Date orderDate;
	    private Date requestedDate;
	    private String status;
	    private String currency;
	    
	    @ManyToOne
	    @JoinColumn(name="userId")
	    private User user;
	    
	    @ManyToOne
	    @JoinColumn(name="customerId")
	    private Customer customer;
	    
	    @OneToMany(mappedBy = "returnOrder")
	    private List<ReturnOrderItem> returnOrderItem;
	    
	    @OneToMany(mappedBy = "returnOrder")
	    private List<OrderAddress> orderAddresses;
		
	    
}
