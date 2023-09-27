package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class QuestionConfig extends BaseEntity {

	private String title;
	private String dataType;
	private String valueJson;
	private boolean isImgMendatory;

	@ManyToOne
	@JsonIgnore
	QuestionMap questionMap;

}