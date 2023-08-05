package com.continuum.tenant.repos.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.continuum.tenant.repos.entity.Permission;

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
public class Roles extends BaseEntity {

	public static final long USER = 1;
	public static final long ADMINISTRATOR = 2;

	//private String role;
	

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "permissions_roles", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	private List<Permission> permissions = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Roles))
			return false;
		return getId() != null && getId().equals(((Roles) o).getId());
	}

	@Override
	public int hashCode() {
		return 31;
	}
//	private String rolename;
//	private String description;
//	private boolean enabled;
}
