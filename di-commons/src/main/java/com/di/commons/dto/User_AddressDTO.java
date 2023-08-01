package com.di.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User_AddressDTO {
	private String Address;
	private String address2;
	private String zipCode;
	private String country;
	private String city;

}
