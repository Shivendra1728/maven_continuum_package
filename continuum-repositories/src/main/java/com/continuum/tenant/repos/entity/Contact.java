package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;

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
public class Contact extends BaseEntity {

	private String contactEmailId;
	private String contactPhoneNo;
	private String contactName;
	private String contactId;

}
