package com.di.commons.dto;

import java.util.List;

import com.continuum.tenant.repos.entity.Store;

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
public class ReasonCodeDTO {

	public Long getParentReasonCodeId() {
		return parentReasonCodeId;
	}
	public void setParentReasonCodeId(Long parentReasonCodeId) {
		this.parentReasonCodeId = parentReasonCodeId;
	}
	private Long id;
	private Long parentReasonCodeId;
	private String code;
	private String description;
	private StoreDTO store;
	private boolean img_mandatory;
	private boolean isPopUp;
	private String popUpDetails;
	
	//private String status;
}
