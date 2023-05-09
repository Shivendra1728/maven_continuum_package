package com.continuum.repos.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReasonCode extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private ReasonCode parentReasonCode;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "parentReasonCode")
	private Set<ReasonCode> childReasonCode;
	
	private String code;
	private String description;
	private String status;
	
	@OneToOne
	@JoinColumn(name = "returnOrderItemId")
	private ReturnOrderItem returnOrderItem;
}
