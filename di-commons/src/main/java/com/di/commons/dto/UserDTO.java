package com.di.commons.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.continuum.repos.entity.UserRole;

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
@Component

public class UserDTO {

	private Long id;
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
	
//	private Customer customer;
//	
//	private List<Orders> orders;

	private List<UserRole> userRoles;

}