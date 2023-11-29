package com.continuum.tenant.repos.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
public class ReturnOrder extends BaseEntity {

	private Long ORMOrder;
	private String salesLocationId;
	private String contactId;
	private String companyId;
	private String PONumber;
	private Date orderDate;
	private Date requestedDate;
	@Column(name = "status")
	private String status;
	private String currency;
	private String orderNo;
	private String invoiceNo;


	@ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
	
    @ManyToOne
    @JoinColumn(name="customerId")
    private Customer customer;

	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "returnOrderId")
	private List<ReturnOrderItem> returnOrderItem;

	   
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="shipTo")
    private OrderAddress shipTo;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name ="billto")
    private OrderAddress billTo;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="con_Id")
    private Contact contact;
    
    private String rmaOrderNo;
    
    @OneToMany(mappedBy = "returnOrder", cascade = CascadeType.ALL)
    private List<RmaInvoiceInfo> rmaInvoiceInfos;
    
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn(name ="return_type")
    private ReturnType returnType;
    
    private boolean ISInvoiceLinked;
    private boolean ISDocumentLinked;

    private Date nextActivityDate;
    private String note;
    private Boolean isEditable;
    private Boolean isAuthorized;
}
