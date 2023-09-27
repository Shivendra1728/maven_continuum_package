package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.QuestionService;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.repositories.QuestionConfigRepository;
import com.continuum.tenant.repos.repositories.QuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepository questionRepository;

	@Autowired
	QuestionConfigRepository questionConfigRepository;

	
	@Override
	public List<QuestionConfig> getQuestions(List<QuestionMap> questionMapList) {
		// TODO Auto-generated method stub
		List<QuestionConfig> questionConfigList = new ArrayList<QuestionConfig>();

		for (QuestionMap map : questionMapList) {
			QuestionConfig questionConfig = new QuestionConfig();
			questionConfigList.add(questionConfig);
		}

		return questionConfigList;
	}


	

}
