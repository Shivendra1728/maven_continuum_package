package com.continuum.tenant.repos.entity;

import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
	
	@Column(name = "questionId")
	private Long questionId;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "returnOrderItemId")
	@JsonIgnore
	private ReturnOrderItem returnOrderItem;

	@Column(name = "answer")
	private String answer;

	@OneToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "questionId",referencedColumnName="id",insertable=false, updatable=false)
	private QuestionConfig questionConfig;
	

}