package com.continuum.service;

import java.util.List;

import com.continuum.tenant.repos.entity.AuditLog;

public interface AuditLogService {
	
	public List<AuditLog> getAll();

	List<AuditLog> getByRmaNo(String rmaNo);

	public AuditLog setAuditLog(String description, String title, String status, String rmaNo, String updateBy,
			String highlight);

}
