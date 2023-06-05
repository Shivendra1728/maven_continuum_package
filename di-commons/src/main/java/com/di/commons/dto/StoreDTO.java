package com.di.commons.dto;

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
public class StoreDTO {
	
	private Long id;
	private String storeName;
	private String StoreCode;
	private StoreAddressDTO shipTo;
	private StoreAddressDTO billTo;

}
