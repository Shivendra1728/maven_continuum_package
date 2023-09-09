package com.di.commons.dto;

import com.continuum.tenant.repos.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDTO {

	private String title;

	private String description;

	private String highlight;
	
	private String status;

	private User userProfile;

}
