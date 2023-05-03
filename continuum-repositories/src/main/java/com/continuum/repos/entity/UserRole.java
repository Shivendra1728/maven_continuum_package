package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

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
public class UserRole  extends BaseEntity{

	@OneToMany
	private List<User> user;
	
	
	@OneToMany
	private List<Roles> roles;
	
}
