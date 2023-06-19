package com.di.integration.p21.transaction;

import javax.xml.bind.annotation.XmlElement;

public class DataElements {

	
	 @XmlElement(name = "Name")
	    private String name;

		/*
		 * @XmlElement(name = "Rows") private Rows rows;
		 */

	    @XmlElement(name = "Type")
	    private String type;
}
