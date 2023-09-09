package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.User;

public interface UserService {

	List<User> createUser(User user);

	List<User> getUserById(Long id);

	String deleteUserById(Long id,String userName);

	String updateUser(Long id,User user);

	

	}
