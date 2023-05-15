package com.di.commons.helper;

import com.di.commons.dto.CustomerDTO;

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
public class OrderSearchParameters {
	
	private String zipcode;
	private String customerId;
	private String poNo;
	private String invoiceNo;

}
