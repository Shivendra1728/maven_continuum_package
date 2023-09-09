package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.AuditLogService;
import com.continuum.tenant.repos.entity.AuditLog;

@RestController()
public class AuditLogController {
	
	@Autowired
	private AuditLogService auditLogService;
	
	@GetMapping("/auditLog/getAll")
	public List<AuditLog> getAll() {
		return auditLogService.getAll();
	}

}
