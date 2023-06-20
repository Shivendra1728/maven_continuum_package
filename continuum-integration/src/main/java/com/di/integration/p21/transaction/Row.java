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
public class Row {

	 @JacksonXmlElementWrapper(localName = "Edits")
	@JacksonXmlProperty(localName = "Edit")
    private List<Edit> edits;

	 @JacksonXmlProperty(localName = "RelativeDateEdits")
    private String relativeDateEdits;

    // Getters and setters
}
