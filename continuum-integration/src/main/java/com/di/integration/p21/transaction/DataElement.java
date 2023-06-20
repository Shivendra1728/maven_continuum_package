package com.di.integration.p21.transaction;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataElement {
	
	@JacksonXmlProperty(localName = "Keys", namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays")
	private String keys;

	@JacksonXmlProperty(localName = "Name")
	private String name;

	@JacksonXmlElementWrapper(localName = "Rows")
	@JacksonXmlProperty(localName = "Row")
	private List<Row> rows;

	@JacksonXmlProperty(localName = "Type")
	private String type;

}
