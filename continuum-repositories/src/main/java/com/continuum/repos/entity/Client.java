package com.continuum.repos.entity;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Client extends BaseEntity {
	
	private String clientName;
	private String ClientAddress;
	private Long contactNo;
	private String city;
	private String state;
	private String country;
	private String email;

}
