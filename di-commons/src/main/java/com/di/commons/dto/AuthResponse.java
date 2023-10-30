package com.di.commons.dto;

import java.io.Serializable;
import java.util.Date;

import com.continuum.tenant.repos.entity.Role;

/**
 * 
 * @author RK
 * 
 */

public class AuthResponse implements Serializable {

	private String userName;

	private Long userId;

	private String token;

	private Role role;
	private String customer;
	private String name;

	private Date expirationDate;
	private String rmaQualifier;

	public AuthResponse(String userName, String name, String token, Role role, long userId, String customer, Date expirationDate,String rmaQualifier) {

		this.userName = userName;
		this.name = name;

		this.token = token;

		this.role = role;

		this.userId = userId;
		this.customer = customer;
		this.expirationDate=expirationDate;
		this.rmaQualifier=rmaQualifier;

	}

	public String getUserName() {

		return userName;

	}

	public AuthResponse setUserName(String userName) {

		this.userName = userName;

		return this;

	}
	
	public String getRmaQualifier() {

		return rmaQualifier;

	}

	public AuthResponse setRmaQualifier(String rmaQualifier) {

		this.rmaQualifier = rmaQualifier;

		return this;

	}

	public String getName() {

		return name;

	}

	public AuthResponse setName(String name) {

		this.name = name;

		return this;

	}

	public String getCustomer() {

		return customer;

	}

	public AuthResponse setCustomer(String customer) {

		this.customer = customer;

		return this;

	}

	public Long getUserId() {

		return userId;

	}

	public AuthResponse setUserId(long userId) {

		this.userId = userId;

		return this;

	}

	public String getToken() {

		return token;

	}

	public Role getRole() {

		return role;

	}

	public void setRole(Role role) {

		this.role = role;

	}

	public AuthResponse setToken(String token) {

		this.token = token;

		return this;

	}
	
	
	public Date getExpirationDate() {

		return expirationDate;

	}

	public AuthResponse setExpirationDate(Date expirationDate) {

		this.expirationDate=expirationDate;

		return this;

	}

}
