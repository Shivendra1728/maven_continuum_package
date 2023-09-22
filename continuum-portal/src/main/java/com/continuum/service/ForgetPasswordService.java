package com.continuum.service;

import javax.servlet.http.HttpServletRequest;

public interface ForgetPasswordService {
	public String forgetPassword(String email,HttpServletRequest request);

	public String updatePassword(String uuid, String password);

}
