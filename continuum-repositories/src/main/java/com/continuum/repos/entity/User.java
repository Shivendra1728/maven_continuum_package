package com.continuum.repos.entity;

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
public class User extends BaseEntity{
	
	private String username;
	private String password;
	private String email;
	private boolean status;
	
	@ManyToOne
	@JoinColumn(name="customerId")
	private Customer customer;
	
	@OneToMany(mappedBy = "user")
	private List<PurchaseOrder> purchaseOrders;
	
	@OneToMany(mappedBy = "user")
	private List<UserRole> userRoles;
	

}
