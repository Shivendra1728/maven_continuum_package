package com.di.commons.dto;

import java.io.Serializable;

import java.util.Set;

import com.continuum.tenant.repos.entity.Role;

import com.continuum.tenant.repos.entity.User;

/**
 * 
 * @author RK
 * 
 */

public class AuthResponse implements Serializable {

	private String userName;

	private String token;

	private Set<Role> role;

	public AuthResponse(String userName, String token, Set<Role> role) {

		this.userName = userName;

		this.token = token;

		this.role = role;

	}

	public String getUserName() {

		return userName;

	}

	public AuthResponse setUserName(String userName) {

		this.userName = userName;

		return this;

	}

	public String getToken() {

		return token;

	}

	public Set<Role> getRole() {

		return role;

	}

	public void setRole(Set<Role> role) {

		this.role = role;

	}

	public AuthResponse setToken(String token) {

		this.token = token;

		return this;

	}

}
