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
public class ReturnOrder extends BaseEntity {

	private Long ORMOrder;
	private String salesLocationId;
	private String contactId;
	private String companyId;
	private String PONumber;
	private Date orderDate;
	private Date requestedDate;
	private String status;
	private String currency;
	private String orderNo;
	private String invoiceNo;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="customerId")
    private Customer customer;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "returnOrderId")
	private List<ReturnOrderItem> returnOrderItem;

	   
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private OrderAddress shipTo;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name ="billto")
    private OrderAddress billTo;


}
