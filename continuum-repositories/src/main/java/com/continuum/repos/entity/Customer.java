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
public class Customer extends BaseEntity{
	
	@ManyToOne
	@JoinColumn(name="storeId")
	private Store store;
	
	private String customerType;
	private boolean status;
	private String firstName;
	private String customerId;
	private String email;
	private String lastname;
	private String displayName;
	private String phone;
	
	@OneToMany(mappedBy = "customer")
	private List<User> users;
	
	@OneToMany(mappedBy = "customer")
	private List<Orders> orders;
	

}
