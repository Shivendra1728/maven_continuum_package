package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
	private String code;
	private String description;
	@JsonIgnore
	private String status;

	// @JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_reason_code_id", insertable = false, updatable = false)
	private ReasonCode parentReasonCode;

	public ReasonCode getParentReasonCode() {
		return parentReasonCode;
	}

	public void setParentReasonCode(ReasonCode parentReasonCode) {
		this.parentReasonCode = parentReasonCode;
	}

	// @JsonManagedReference
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parentReasonCode")
	private List<ReasonCode> childReasonCodes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store")
	// @JsonIgnore
	private Store store;
	
	@Builder.Default
	private boolean img_mandatory=true;
	
	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "customer") //@JsonIgnore private Customer customer;
	 */

}
