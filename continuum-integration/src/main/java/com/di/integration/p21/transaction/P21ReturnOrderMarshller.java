package com.di.integration.p21.transaction;



import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;
@Component
public class P21ReturnOrderMarshller {
	
	
    public String  prepareXml() throws JAXBException {
       TransactionSet transactionSet = new TransactionSet();
       transactionSet.setName("RMA");
        // Set values for the transactionSet and its child objects
        
        // Create JAXB context and marshaller
        JAXBContext jaxbContext = JAXBContext.newInstance(TransactionSet.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        // Marshal the transactionSet to XML string
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(transactionSet, stringWriter);

        String xmlPayload = stringWriter.toString();
        System.out.println(xmlPayload);
        return xmlPayload;
    }
}
