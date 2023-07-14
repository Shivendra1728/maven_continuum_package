package com.di.integration.p21.transaction;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;
@JacksonXmlRootElement(localName = "OrderNote")
@Getter
@Setter
public class OrderNote {
	  
	@JacksonXmlProperty(localName = "Topic")
	    private String topic;

	    @JacksonXmlProperty(localName = "Note")
	    private String note;

	    @JacksonXmlProperty(localName = "NotepadClassId")
	    private String notepadClassId;

	    @JacksonXmlProperty(localName = "OrderNo")
	    private String orderNo;

	    @JacksonXmlProperty(localName = "Mandatory")
	    private boolean mandatory;
}
