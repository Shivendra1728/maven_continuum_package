package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class User_Address extends BaseEntity {

	private String address;
	private String address2;
	private String zipCode;
	private String country;
	private String state;
	private String city;
}
