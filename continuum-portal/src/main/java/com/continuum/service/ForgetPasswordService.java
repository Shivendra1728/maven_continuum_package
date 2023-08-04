package com.continuum.service;

import java.util.UUID;

public interface ForgetPasswordService {
	public String forgetPassword(String email);

	public String updatePassword(String uuid, String password);

}
