package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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
	private String firstName;
	private String lastName;
	private String contactInfo;
	private long contactNo;
	private long alternateNo;
	private String address;
	private String city;
	private String country;
	private String pinCode;
	
	
	private String gender;
//	
//	@ManyToOne
//	@JoinColumn(name="customerId")
//	private Customer customer;
//	
//	@OneToMany(mappedBy = "user")
//	private List<Orders> orders;
	
	@OneToMany(mappedBy = "user")
	private List<UserRole> userRoles;
	
	

	


	
	

	

	

}
