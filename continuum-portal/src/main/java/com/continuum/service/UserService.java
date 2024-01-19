package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.TNC;
import com.continuum.tenant.repos.entity.User;

public interface UserService {

	Long createUser(User user) throws Exception;

	List<User> getUserById(Long id);

	String deleteUserById(Long id, String userName);

	String updateUser(Long id, User user);

	List<TNC> getTnc();

}
