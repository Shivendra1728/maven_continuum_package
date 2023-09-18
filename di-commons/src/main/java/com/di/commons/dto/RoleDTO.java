package com.di.commons.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.continuum.tenant.repos.entity.Role;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoleDTO implements Serializable {

	private Long id;
	private String role;

	private List<PermissionDTO> permissions = new ArrayList<>();

	public RoleDTO(Role role) {
		this.id = role.getId();
		this.role = role.getRole();

		// permissions
		role.getPermissions().stream().forEach(e -> permissions.add(new PermissionDTO(e)));
		// Pages

	}

	public RoleDTO(Long id, String role) {
		this.id = id;
		this.role = role;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RoleDTO))
			return false;
		return id != null && id.equals(((RoleDTO) obj).getId());
	}

	@Override
	public int hashCode() {
		return 31;
	}

}
