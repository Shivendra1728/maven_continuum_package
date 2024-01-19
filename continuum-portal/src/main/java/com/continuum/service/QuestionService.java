package com.continuum.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.ReturnOrderItem;

@Service
public interface QuestionService {

	List<QuestionMap> getQuestions(ReturnOrderItem returnOrderItem);

}
