package com.di.commons.helper;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P21InvoiceData {

	@JsonProperty("odata.metadata")
	private String odataMetadata;

	private String bill2_postal_code;
	private String invoice_no;
	private String order_no;
	public String invoice_date;
	private String bill2_city;

}
