package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

	@JacksonXmlElementWrapper(localName = "DataElements")
	@JacksonXmlProperty(localName = "DataElement")
    private List<DataElement> dataElements;
}
