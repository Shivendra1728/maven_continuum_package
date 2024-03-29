package com.di.commons.helper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class P21OrderLineItem {

	private String item_desc;
	private String item_id;
	private String ordered_qty;
	private String unit_price;
	private String original_invoice_no;
	private String invoice_no;
	private String oe_line_uid;
	private String order_no;
	private String original_invoice_date;
	private String line_number;
	private String parent_oe_line_uid;
	private String other_charge;
}
