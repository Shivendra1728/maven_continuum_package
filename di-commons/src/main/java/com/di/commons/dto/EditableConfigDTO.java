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


public class EditableConfigDTO {
	
	private String assignRMA;
	private String changeStatus;
	private String restockingFees;
	private String lineLevelStatus;
	private String addNotes;
	private String shippingInfo;
	private String problemDescription;
	private String trackingCode;
	private String amountAddition;
	private String itemDeletion;
	private String recievedStatus;

}
