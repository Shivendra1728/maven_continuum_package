package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusRelation extends BaseEntity {

	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "status_config_id")
	private StatusConfig statusConfig;
	
	private Long StatusId;

	private Boolean editable;

}
