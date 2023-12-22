package com.continuum.service;

import javax.servlet.http.HttpServletRequest;

import com.continuum.tenant.repos.entity.Role;

public interface ForgetPasswordService {
	public String forgetPassword(String email,HttpServletRequest request);

	public Role updatePassword(String uuid, String password);

}
