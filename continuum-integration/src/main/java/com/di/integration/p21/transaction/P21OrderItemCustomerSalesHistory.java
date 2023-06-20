package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class P21OrderItemCustomerSalesHistory {

	private String order_no;
	private String cc_invoice_no_display;
	private String location_id;
}
