package com.di.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RmaInvoiceInfoDTO {

	private String rmaOrderNo;
	private boolean isInvoiceLinked;
	private boolean isDocumentLinked;
	private String description;
	private Integer retryCount;

	private ReturnOrderDTO returnOrderDTO;

}
