package com.di.commons.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
public class OrderAddressDTO {

	
	private Long id;
	private Date createdDate;
	private Date updatedDate;
	private String phoneNumber;
	private String fax;
	private String street1;
	private String street2;
	private String country;
	private String province;
	private String city;
	private String zipcode;
	private String addressType;
}
