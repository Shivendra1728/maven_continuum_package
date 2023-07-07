package com.di.commons.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class ClientDTO {
	private Long id;
	private String clientName;
	private String ClientAddress;
	private Long contactNo;
	private String city;
	private String state;
	private String country;
	private String email;
}
