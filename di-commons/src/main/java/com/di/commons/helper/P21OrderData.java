package com.di.commons.helper;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P21OrderData {
	
	private String order_no;
	private String po_number;
	private String location_id;
	private String currency_desc;
	private String original_invoice_no;
	private String date_created;
	private String requested_date;
	private String order_date;
	private String customer_id;
	private String order_contact_first_name;
	private String order_contact_last_name;
	private String contact_email_address;
	private String order_contact_name;
	private String contact_phone_number;
	private String contact_fax_number;
	private String ship2_add1;
	private String ship2_add2;
	private String ship2_country;
	private String ship2_city;
	private String ship2_state;
	private String ship2_zip;
	private String mail_address1_a;
	private String mail_address2_a;
	private String mail_country_a;
	private String mail_state_a;
	private String mail_city_a;
	private String mail_postal_code_a;
	@JsonProperty("odata.metadata")
	private String odataMetadata;
	
	private String address_id;
	private String company_id;
}
