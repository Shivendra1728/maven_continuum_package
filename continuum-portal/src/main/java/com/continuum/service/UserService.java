package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.User;

public interface UserService {

	String createUser(User user);

	List<User> getUserById(Long id);

	String deleteUserById(Long id);

	String updateUser(Long id,User user);

	

	}
