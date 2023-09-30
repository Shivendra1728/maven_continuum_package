package com.continuum.tenant.repos.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private QuestionMap questionMap;
	
}

