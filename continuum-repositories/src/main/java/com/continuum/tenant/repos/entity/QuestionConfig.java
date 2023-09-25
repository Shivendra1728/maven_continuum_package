package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class QuestionConfig extends BaseEntity {

	private String title;
	private String dataType;
	private String valueJson;
	private boolean isImgMendatory;
}
