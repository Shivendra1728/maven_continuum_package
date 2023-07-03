package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Edit {

	@JacksonXmlProperty(localName  = "Name")
    private String name;

	@JacksonXmlProperty(localName = "Value")
    private String value;
	
	
	

}
