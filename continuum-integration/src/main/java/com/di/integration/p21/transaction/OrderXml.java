package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Order")
@Getter
@Setter
public class OrderXml {
    @JacksonXmlProperty(localName = "CustomerId")
    private String customerId;

    @JacksonXmlProperty(localName = "CompanyId")
    private String companyId;

    @JacksonXmlProperty(localName = "LocationId")
    private String locationId;

    @JacksonXmlProperty(localName = "ContactId")
    private String contactId;

    @JacksonXmlProperty(localName = "OrderNo")
    private String orderNo;

    @JacksonXmlProperty(localName = "OrderNote")
    @JacksonXmlElementWrapper(localName = "Notes")
    private List<OrderNote> orderNotes;

    // Getters and setters
}


