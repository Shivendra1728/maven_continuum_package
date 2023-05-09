package com.di.commons.dto;

import java.util.Date;

import com.continuum.repos.entity.ReturnOrderItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

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
