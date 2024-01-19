package com.continuum.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.continuum.service.QuestionService;
import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.repositories.QuestionConfigRepository;
import com.continuum.tenant.repos.repositories.QuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepository questionRepository;

	@Autowired
	QuestionConfigRepository questionConfigRepository;

	@Override
	public List<QuestionMap> getQuestions(ReturnOrderItem returnOrderItemId) {

		questionRepository.findQuestionMapByReturnOrderItem(returnOrderItemId);

		return null;

	}

}
