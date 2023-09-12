package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.AuditLog;

public interface AuditLogService {
	
	public List<AuditLog> getAll();

	List<AuditLog> getByRmaNo(String rmaNo);

}
