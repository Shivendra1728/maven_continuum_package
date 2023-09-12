package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.AuditLogService;
import com.continuum.service.ReturnRoomService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.ReturnRoom;

@RestController()
public class ReturnRoomController {
	
	@Autowired
	private ReturnRoomService returnRoomService;
	
	@GetMapping("/returnRoom/getAll")
	public List<ReturnRoom> getAll() {
		return returnRoomService.getAll();
	}
	
	@GetMapping("/returnRoom/getById")
	public List<ReturnRoom> getById(@RequestParam Long returnOrderItemId) {
		return returnRoomService.getById(returnOrderItemId);
	}

}
