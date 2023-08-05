package com.continuum.service;

import com.continuum.repos.entity.User;

public interface LoginService {
	String getUserByUsernameOrEmail(String usernameOrEmail, String password);

	User getUserByUsernameOrEmail(String username);
}