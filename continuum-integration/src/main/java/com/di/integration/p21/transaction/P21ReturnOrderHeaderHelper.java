package com.di.integration.p21.transaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class P21ReturnOrderHeaderHelper {

	
private String company_id;
private String customer_id;
private String sales_loc_id;
private String ship_to_id;
private String contact_id;
private String po_no;
private String taker;
private String order_contact_first_name;

}
