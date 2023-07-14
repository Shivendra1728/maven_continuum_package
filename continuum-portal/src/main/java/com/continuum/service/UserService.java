package com.continuum.service;

import com.continuum.repos.entity.User;

public interface UserService {

	String getUserByUsernameOrEmail(String usernameOrEmail, String password);

}