package com.di.commons.dto;

import java.util.List;

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

	private Long id;
	private ReasonCodeDTO parentReasonCode;
	private List<ReasonCodeDTO> childReasonCodes;
	private String code;
	private String description;
	private String status;
}
