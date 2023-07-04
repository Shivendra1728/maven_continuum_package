package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDataElements {

	

	 @JsonProperty("Name")
	private String name;

	 @JsonProperty("Rows")
	private List<ResponseRows> rows;

	 @JsonProperty("Type")
	private String type;
}
