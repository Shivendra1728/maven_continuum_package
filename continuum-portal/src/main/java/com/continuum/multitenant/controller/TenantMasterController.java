package com.continuum.multitenant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.service.MasterTenantService;

@RestController
@RequestMapping("/api/tenant")
public class TenantMasterController {
	@Autowired
	MasterTenantService tenantMasterservice;
	 @RequestMapping(value = "/all", method = RequestMethod.GET)
	    public ResponseEntity<List<MasterTenant>> getAllProduct() {
	        return new ResponseEntity<>(tenantMasterservice.getAllTenants(),HttpStatus.OK);
	    }
	    
	    
}
