package com.di.commons.dto;

import com.continuum.repos.entity.Permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {

	public PermissionDTO(Permission e) {
		// TODO Auto-generated constructor stub
	}
	private String permission;
    private boolean enabled;
    private String note;
}
