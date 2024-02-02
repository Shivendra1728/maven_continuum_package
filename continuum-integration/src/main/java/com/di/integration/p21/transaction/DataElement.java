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
public class DataElement {
	

	@JacksonXmlProperty(localName = "Keys", namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays")
	private Keys keys;

	@JacksonXmlProperty(localName = "Name")
	private String name;

	@JacksonXmlElementWrapper(localName = "Rows")
	@JacksonXmlProperty(localName = "Row")
	private List<Row> rows;

	@JacksonXmlProperty(localName = "Type")
	private String type;
	

}
