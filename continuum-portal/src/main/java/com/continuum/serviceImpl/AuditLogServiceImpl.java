package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.AuditLogService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.repositories.AuditLogRepository;

@Service
public class AuditLogServiceImpl implements AuditLogService {
	@Autowired
	AuditLogRepository auditLogRepository;

	@Override
	public List<AuditLog> getAll() {
		List<AuditLog> auditLogs = auditLogRepository.findAll();
		return auditLogs;
	}

	@Override
	public List<AuditLog> getByRmaNo(String rmaNo) {
		return auditLogRepository.findByRmaNo(rmaNo);
	}

	@Override
	public AuditLog setAuditLog(String description, String title, String status, String rmaNumber, String updateBy,
			String highlight) {
		AuditLog auditLog = new AuditLog();

		auditLog.setDescription(description);
		auditLog.setHighlight(highlight);
		auditLog.setTitle(title);
		auditLog.setStatus(status);
		auditLog.setRmaNo(rmaNumber);
		auditLog.setUserName(updateBy);
		auditLogRepository.save(auditLog);

		return auditLog;
	}
}
