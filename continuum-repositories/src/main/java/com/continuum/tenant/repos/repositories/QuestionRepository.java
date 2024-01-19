package com.continuum.tenant.repos.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.ReturnOrderItem;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionMap, Long> {

	List<QuestionMap> findQuestionMapByReturnOrderItem(ReturnOrderItem returnOrderItem);

}