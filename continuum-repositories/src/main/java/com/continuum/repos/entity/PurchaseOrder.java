package com.continuum.repos.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class PurchaseOrder  extends BaseEntity{
	
	  
	    private Long ORMOrder;
	    private Long salesLocationId;
	
	    private Long contactId;
	    private String PONumber;
	    private Date orderDate;
	    private Date requestedDate;
	    private String status;
	    private String currency;
	    private String invoiceNo;
	    
	    @ManyToOne
	    @JoinColumn(name="userId")
	    private User user;
	    
	    @ManyToOne
	    @JoinColumn(name="customerId")
	    private Customer customer;
	    
	    @OneToMany( cascade = CascadeType.ALL)
	    @JoinColumn(name="purchaseOrder")
	    private List<PurchaseOrderItem> purchaseOrderItems;
	    
	    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	    @JoinColumn(name ="shipTo")
	    private OrderAddress shipTo;
	    
	    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	    @JoinColumn(name ="billto")
	    private OrderAddress billTo;
}
