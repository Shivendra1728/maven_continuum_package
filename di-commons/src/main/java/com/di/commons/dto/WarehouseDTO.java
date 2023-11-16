package com.di.commons.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class WarehouseDTO {
	private String street1;
	private String street2;
	private String country;
	private String province;
	private String city;
	private String state;
	private String zipcode;
	private String addressType;

}
