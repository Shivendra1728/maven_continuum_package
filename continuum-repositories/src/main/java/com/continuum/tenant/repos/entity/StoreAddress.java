package com.continuum.tenant.repos.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

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
public class StoreAddress extends BaseEntity {

	
	private String phoneNumber;
	private String fax;
	private String street1;
	private String street2;
	private String country;
	private String province;
	private String city;
	private String zipcode;
	private String email;
	private String addressType;
	
}
