package com.di.commons.dto;

import java.util.Date;

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

	private String addressId;
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
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String returnLocNote;
	private String attentionNote;


}
