package com.di.commons.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OrderItemDocumentsDTO {
	private Long id;
	private String URL;
	private String type;
	private String status;
}
