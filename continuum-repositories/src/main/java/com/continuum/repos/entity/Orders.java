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

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Orders  extends BaseEntity{
	
	  
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
	    
	    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	    @JoinColumn(name="customerId")
	    private Customer customer;
	    
	    @OneToMany( fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	    @JoinColumn(name="orderId")
	    private List<OrderItem> orderItems;
	    
	    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	    @JoinColumn(name ="shipTo")
	    private OrderAddress shipTo;
	    
	    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	    @JoinColumn(name ="billto")
	    private OrderAddress billTo;
}
