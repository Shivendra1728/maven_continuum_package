package com.continuum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.QuestionMap;

@Service
public interface QuestionService{
	
	

	List<QuestionConfig> getQuestions(List<QuestionMap> questionMapList);

}
