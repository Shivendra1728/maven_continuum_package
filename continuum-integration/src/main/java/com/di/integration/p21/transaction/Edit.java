package com.di.integration.p21.transaction;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Edit {

	@JacksonXmlProperty(localName  = "Name")
    private String name;

	@JacksonXmlProperty(localName = "Value")
    private String value;

}
