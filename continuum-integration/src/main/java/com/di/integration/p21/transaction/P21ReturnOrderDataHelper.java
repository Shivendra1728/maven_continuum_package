package com.di.integration.p21.transaction;

import java.util.List;

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
public class P21ReturnOrderDataHelper {

	private P21ReturnOrderHeaderHelper p21OrderHeader;
	private List<P21OrderItemHelper> p21OrderItemList;
	private List<String> reasonCodes;
	private List<String> probDescList;
	private P21OrderItemCustomerSalesHistory p21OrderItemCustSalesHistory;
	
}
