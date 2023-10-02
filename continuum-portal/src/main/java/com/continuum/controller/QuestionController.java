package com.continuum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.service.QuestionService;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.ReturnOrderItem;

@RestController
@RequestMapping("/question")
public class QuestionController {
	
	@Autowired
	QuestionService questionService;


	@GetMapping("/get")
	public List<QuestionMap> getQuetions(@RequestParam ReturnOrderItem returnOrderItem) {
		return questionService.getQuestions(returnOrderItem);

	}

}