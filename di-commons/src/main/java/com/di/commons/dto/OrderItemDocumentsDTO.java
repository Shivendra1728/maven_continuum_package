package com.di.commons.dto;

import java.util.Date;

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
public class OrderItemDocumentsDTO {
	private long id;
	private String URL;
	private String type;
	private String status;
}
