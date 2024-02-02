package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor

public class EditableConfig extends BaseEntity {

	private boolean assignRMA;
	private boolean changeStatus;
	private boolean restockingFees;
	private boolean lineLevelStatus;
	private boolean addNotes;
	private boolean shippingInfo;
	private boolean problemDescription;
	private boolean trackingCode;
	private boolean amountAddition;
	private boolean itemDeletion;
	private boolean searchChildSKU;
	private boolean recievedStatus;
	private boolean isSerialized;
	private boolean isSellable;
	private boolean addSku;

}
