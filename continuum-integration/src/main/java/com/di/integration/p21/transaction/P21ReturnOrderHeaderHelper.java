package com.di.integration.p21.transaction;

import java.util.Date;
import java.util.List;

import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
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
}
