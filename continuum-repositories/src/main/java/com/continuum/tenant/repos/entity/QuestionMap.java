package com.continuum.tenant.repos.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity

@Getter

@Setter

@NoArgsConstructor

@Table(name = "question_map")

public class QuestionMap {

	@Id

	@GeneratedValue(strategy = GenerationType.AUTO)

	private Long id;

	@Column(name = "rmaNo")

	private Long rmaNo;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "returnOrderItemId")
	private ReturnOrderItem returnOrderItem;

	@Column(name = "question_id")

	private Long questionId;

	@Column(name = "answer")

	private String answer;

	@OneToMany(mappedBy = "questionMap", cascade = CascadeType.ALL)
	private List<QuestionConfig> questionConfigs;

	

}