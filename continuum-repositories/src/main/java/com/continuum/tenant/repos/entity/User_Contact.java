package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User_Contact extends BaseEntity {

	private String email;
	private String phone;
	private String alternativeNumber;

}
