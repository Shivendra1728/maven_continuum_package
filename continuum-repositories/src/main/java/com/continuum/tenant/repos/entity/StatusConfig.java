package com.continuum.tenant.repos.entity;

import javax.persistence.Column;
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
public class StatusConfig extends BaseEntity {

	@Column(name = "status_type")
	private String statusType;
	private String statuslabl;
	private String color;
	private String backgroundColor;
	private boolean isTrackingAvl;
	private boolean isEmailsend;
	private boolean isInitial;
	private boolean Display;
	private Boolean isEditable;
	private Boolean isAuthorized;
	private int priority;
	private String statusMap;
	private String isRecieved;
}
