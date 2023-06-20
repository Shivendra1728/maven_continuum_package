package com.di.integration.p21.transaction;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;
	
	@JacksonXmlRootElement(localName = "TransactionSet")
	@Getter
	@Setter
	public class TransactionSet {

		 @JacksonXmlProperty(isAttribute = true)
		    private final String xmlns = "http://schemas.datacontract.org/2004/07/P21.Transactions.Model.V2";

		@JacksonXmlProperty(localName = "IgnoreDisabled")
	    private boolean ignoreDisabled;

	    @JacksonXmlProperty(localName = "Name")
	    private String name;

	    @JacksonXmlElementWrapper(localName = "Transactions")
	    @JacksonXmlProperty(localName = "Transaction")
	    private List<Transaction> transactions;

	}


	

	

	


	/*
	 * public static String prepareXMl() { try { DocumentBuilderFactory factory =
	 * DocumentBuilderFactory.newInstance(); DocumentBuilder builder =
	 * factory.newDocumentBuilder();
	 * 
	 * // Create a new Document Document document = builder.newDocument();
	 * 
	 * // Create the root element <TransactionSet> Element transactionSetElement =
	 * document.createElement("TransactionSet");
	 * transactionSetElement.setAttribute("xmlns",
	 * "http://schemas.datacontract.org/2004/07/P21.Transactions.Model.V2");
	 * document.appendChild(transactionSetElement);
	 * 
	 * // Create <IgnoreDisabled> element Element ignoreDisabledElement =
	 * document.createElement("IgnoreDisabled");
	 * ignoreDisabledElement.setTextContent("true");
	 * transactionSetElement.appendChild(ignoreDisabledElement);
	 * 
	 * // Create <Name> element Element nameElement =
	 * document.createElement("Name"); nameElement.setTextContent("RMA");
	 * transactionSetElement.appendChild(nameElement);
	 * 
	 * // Create <Transactions> element Element transactionsElement =
	 * document.createElement("Transactions");
	 * transactionSetElement.appendChild(transactionsElement);
	 * 
	 * // Create <Transaction> element Element transactionElement =
	 * document.createElement("Transaction");
	 * transactionsElement.appendChild(transactionElement);
	 * 
	 * // Create <DataElements> element Element dataElementsElement =
	 * document.createElement("DataElements");
	 * transactionElement.appendChild(dataElementsElement);
	 * 
	 * // Create first <DataElement> element Element dataElement1 =
	 * document.createElement("DataElement");
	 * dataElementsElement.appendChild(dataElement1);
	 * 
	 * // Create <Keys> element Element keysElement1 =
	 * document.createElement("Keys"); keysElement1.setAttribute("xmlns:a",
	 * "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
	 * dataElement1.appendChild(keysElement1);
	 * 
	 * // Create <Name> element Element nameElement1 =
	 * document.createElement("Name");
	 * nameElement1.setTextContent("TABPAGE_1.order");
	 * dataElement1.appendChild(nameElement1);
	 * 
	 * // Create <Rows> element Element rowsElement1 =
	 * document.createElement("Rows"); dataElement1.appendChild(rowsElement1);
	 * 
	 * // Create <Row> element Element rowElement1 = document.createElement("Row");
	 * rowsElement1.appendChild(rowElement1);
	 * 
	 * // Create <Edits> element Element editsElement1 =
	 * document.createElement("Edits"); rowElement1.appendChild(editsElement1);
	 * 
	 * // Create <Edit> elements createEditElement(document, editsElement1,
	 * "company_id", "LD001"); createEditElement(document, editsElement1,
	 * "customer_id", "155985"); createEditElement(document, editsElement1,
	 * "sales_loc_id", "101"); createEditElement(document, editsElement1, "taker",
	 * "Continuum");
	 * 
	 * // Create second <DataElement> element Element dataElement2 =
	 * document.createElement("DataElement");
	 * dataElementsElement.appendChild(dataElement2);
	 * 
	 * // Create <Keys> element Element keysElement2 =
	 * document.createElement("Keys"); keysElement2.setAttribute("xmlns:a",
	 * "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
	 * dataElement2.appendChild(keysElement2);
	 * 
	 * // Create <Name> element Element nameElement2 =
	 * document.createElement("Name");
	 * nameElement2.setTextContent("TP_ITEMS.items");
	 * dataElement2.appendChild(nameElement2);
	 * 
	 * // Create <Rows> element Element rowsElement2 =
	 * document.createElement("Rows"); dataElement2.appendChild(rowsElement2);
	 * 
	 * // Create <Row> element Element rowElement2 = document.createElement("Row");
	 * rowsElement2.appendChild(rowElement2);
	 * 
	 * // Create <Edits> element Element editsElement2 =
	 * document.createElement("Edits"); rowElement2.appendChild(editsElement2);
	 * 
	 * // Create <Edit> elements createEditElement(document, editsElement2,
	 * "oe_order_item_id", "BG1000-250"); createEditElement(document, editsElement2,
	 * "unit_quantity", "1");
	 * 
	 * // Create third <DataElement> element Element dataElement3 =
	 * document.createElement("DataElement");
	 * dataElementsElement.appendChild(dataElement3);
	 * 
	 * // Create <Keys> element Element keysElement3 =
	 * document.createElement("Keys"); keysElement3.setAttribute("xmlns:a",
	 * "http://schemas.microsoft.com/2003/10/Serialization/Arrays");
	 * dataElement3.appendChild(keysElement3);
	 * 
	 * // Create <Name> element Element nameElement3 =
	 * document.createElement("Name");
	 * nameElement3.setTextContent("REASONCODESHDR.reasoncodeshdr");
	 * dataElement3.appendChild(nameElement3);
	 * 
	 * // Create <Rows> element Element rowsElement3 =
	 * document.createElement("Rows"); dataElement3.appendChild(rowsElement3);
	 * 
	 * // Create <Row> element Element rowElement3 = document.createElement("Row");
	 * rowsElement3.appendChild(rowElement3);
	 * 
	 * // Create <Edits> element Element editsElement3 =
	 * document.createElement("Edits"); rowElement3.appendChild(editsElement3);
	 * 
	 * // Create <Edit> element createEditElement(document, editsElement3,
	 * "lost_sales_id", "ALTERNATE SUPPLIER");
	 * 
	 * // Print the XML String xmlString = toXmlString(document);
	 * System.out.println(xmlString);
	 * 
	 * } catch (ParserConfigurationException | TransformerException e) {
	 * e.printStackTrace(); } return null; }
	 * 
	 * private static void createEditElement(Document document, Element
	 * parentElement, String name, String value) { Element editElement =
	 * document.createElement("Edit"); parentElement.appendChild(editElement);
	 * 
	 * Element nameElement = document.createElement("Name");
	 * nameElement.setTextContent(name); editElement.appendChild(nameElement);
	 * 
	 * Element valueElement = document.createElement("Value");
	 * valueElement.setTextContent(value); editElement.appendChild(valueElement); }
	 * 
	 * private static String toXmlString(Document document) throws
	 * TransformerException { TransformerFactory transformerFactory =
	 * TransformerFactory.newInstance(); Transformer transformer =
	 * transformerFactory.newTransformer(); StringWriter writer = new
	 * StringWriter(); transformer.transform(new DOMSource(document), new
	 * StreamResult(writer)); return writer.toString(); }
	 */
//}





