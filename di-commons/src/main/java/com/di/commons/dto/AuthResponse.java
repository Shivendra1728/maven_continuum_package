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

	private Long userId;

	private String token;

	private Role role;

	public AuthResponse(String userName, String token, Role role, long userId) {

		this.userName = userName;

		this.token = token;

		this.role = role;

		this.userId = userId;

	}

	public String getUserName() {

		return userName;

	}

	public AuthResponse setUserName(String userName) {

		this.userName = userName;

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

}
