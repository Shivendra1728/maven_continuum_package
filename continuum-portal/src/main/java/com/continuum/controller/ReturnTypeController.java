package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.ReturnTypeService;
import com.continuum.tenant.repos.entity.ReturnType;

@RestController
@RequestMapping("/returnType")
public class ReturnTypeController {

	@Autowired
	ReturnTypeService returnTypeService;

	@GetMapping("/getAll")
	public List<ReturnType> getAllReturnType() throws Exception {

		return returnTypeService.getAll();

	}
}
