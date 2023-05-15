package com.di.commons.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class CustomerDTO {

//	private Store store;
	private String customerType;
	private boolean status;
	private String firstName;
	private String customerId;
	private String email;
	private String lastname;
	private String displayName;
	
	//private List<User> users;
	
	//private List<PurchaseOrder> purchaseOrders
}
