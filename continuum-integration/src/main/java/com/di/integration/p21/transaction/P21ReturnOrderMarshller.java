package com.di.integration.p21.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.di.integration.constants.IntegrationConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
@Component
public class P21ReturnOrderMarshller {

	private static final Logger logger = LoggerFactory.getLogger(P21ReturnOrderMarshller.class);

	public String createRMA(P21ReturnOrderDataHelper p21ReturnOrderDataHelper)
			throws JAXBException, JsonProcessingException {
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// xmlMapper.disable(MapperFeature.USE_STD_BEAN_NAMING);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// xmlMapper.setDefaultUseWrapper(false);
		String xml = xmlMapper.writeValueAsString(prepareXMl(p21ReturnOrderDataHelper));
		xml = xml.replaceAll("wstxns2:", "");
		xml = xml.replaceAll("xmlns:wstxns2", "xmlns:a");

		xml = xml.replaceAll("wstxns1:", "");
		xml = xml.replaceAll("xmlns:wstxns1", "xmlns:a");

		xml = xml.replaceAll("wstxns3:", "");
		xml = xml.replaceAll("xmlns:wstxns3", "xmlns:a");
		
		xml = xml.replaceAll("wstxns4:", "");
		xml = xml.replaceAll("xmlns:wstxns4", "xmlns:a");
		
		logger.info(xml);
		return xml;
	}
	
public P21RMAResponse umMarshall(String jsonString) throws JsonMappingException, JsonProcessingException {
	
	P21RMAResponse p21RMAResp= new P21RMAResponse();
	// Assuming the JSON string is stored in a variable called jsonString
	ObjectMapper objectMapper = new ObjectMapper();
	objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	RootObject rootObject = objectMapper.readValue(jsonString, RootObject.class);

	// Access the unmarshalled data
	//System.out.println("Name: " + rootObject.getResults().getName());
	//System.out.println("getSummary faild: " + rootObject.getSummary().getFailed());
	logger.info("Name: {}", rootObject.getResults().getName());
	logger.info("getSummary failed: {}", rootObject.getSummary().getFailed());

	if(rootObject.getSummary().getFailed()==1) {
		p21RMAResp.setStatus(IntegrationConstants.FAILED);
	}
	if(rootObject.getSummary().getSucceeded()==1) {
		p21RMAResp.setStatus(IntegrationConstants.SUCCESS);
	}
	p21RMAResp.setMessages(rootObject.getMessages());
	//System.out.println("IgnoreDisabled: " + rootObject.isIgnoreDisabled());

	// Access Transactions
	List<ResponseTransaction> transactions = rootObject.getResults().getTransactions();
	for (ResponseTransaction transaction : transactions) {
	 //  System.out.println("Transaction Status: " + transaction.ge);
	    
	    List<ResponseDataElements> dataElements = transaction.getDataElements();
	    for (ResponseDataElements dataElement : dataElements) {
	       // System.out.println("Data Element Name: " + dataElement.getName());
	    	logger.info("Data Element Name: {}", dataElement.getName());
	    	
	        List<ResponseRows> rows = dataElement.getRows();
	        for (ResponseRows row : rows) {
	            List<ResponseEdit> edits = row.getEdits();
	            for (ResponseEdit edit : edits) {
	               
	            	logger.info("Edit Name: {}", edit.getName());
	            	if("order_no".equalsIgnoreCase(edit.getName())) {
	                	p21RMAResp.setRmaOrderNo(edit.getValue());
	                }
	              
	            	logger.info("Edit Value: {}", edit.getValue());
	            }
	        }
	    }
	}
	return p21RMAResp;

	// Access Summary
	/*
	 * Summary summary = rootObject.getSummary(); System.out.println("Failed: " +
	 * summary.getFailed()); System.out.println("Succeeded: " +
	 * summary.getSucceeded()); System.out.println("Other: " + summary.getOther());
	 */

}

	private TransactionSet prepareXMl(P21ReturnOrderDataHelper p21ReturnOrderDataHelper) {
		TransactionSet transactionSet = new TransactionSet();
		transactionSet.setIgnoreDisabled(true);
		transactionSet.setName(IntegrationConstants.RMA);
		Transaction transaction = new Transaction();
		
		List<DataElement> dataElements= new ArrayList<>();

		// ORDER HEADER DATA ELEMENT 1-----------------------------------------
		DataElement dataElement1 = new DataElement();
		dataElement1.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER);
		dataElement1.setType(IntegrationConstants.DATA_ELEMENT_TYPE_FORM);

		Row row1 = new Row();

		Edit edit1 = new Edit();
		edit1.setName(IntegrationConstants.EDIT_NAME_COMPANY_ID);
		edit1.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getCompany_id());

		Edit edit2 = new Edit();
		edit2.setName(IntegrationConstants.EDIT_NAME_CUSTOMER_ID);
		edit2.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getCustomer_id());
		
		Edit edit3 = new Edit();
		edit3.setName(IntegrationConstants.EDIT_NAME_SALES_LOC_ID);
		edit3.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getSales_loc_id());

		Edit edit4 = new Edit();
		edit4.setName("ship_to_id");
		edit4.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getShip_to_id());

		Edit edit5 = new Edit();
		edit5.setName("contact_id");
		edit5.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getContact_id());

		Edit edit6 = new Edit();
		edit6.setName("po_no");
		edit6.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getPo_no());

		Edit edit7 = new Edit();
		edit7.setName("taker");
		edit7.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getTaker());
		
		//Edit edit8 = new Edit();
		//edit8.setName("order_contact_first_name");
		//edit8.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getOrder_contact_first_name());

		// Add the Edit objects to the Row object
		row1.setEdits(Arrays.asList(edit1, edit2, edit3, edit4, edit5, edit6, edit7));

		// Add the Row objects to the DataElement objects
		dataElement1.setRows(Collections.singletonList(row1));
		dataElements.add(dataElement1);
		// ORDERITEM DATA ELEMENT 1-----------------------------------------

		DataElement dataElement2 = new DataElement();
		dataElement2.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER_ITEMS);
		dataElement2.setType(IntegrationConstants.DATA_ELEMENT_TYPE_LIST);
		List<Row> rows= new ArrayList<>();
		for (P21OrderItemHelper p21OrderItemHelper : p21ReturnOrderDataHelper.getP21OrderItemList()) {
			Row itemRow = new Row();

			// Create the Edit objects
			Edit itemEdit1 = new Edit();
			itemEdit1.setName(IntegrationConstants.EDIT_NAME_OE_ORDER_ITEM_ID);
			itemEdit1.setValue(p21OrderItemHelper.getOe_order_item_id());

			Edit itemEdit2 = new Edit();
			itemEdit2.setName(IntegrationConstants.EDIT_NAME_UNIT_QUANTITY);
			itemEdit2.setValue(p21OrderItemHelper.getUnit_quantity());

			itemRow.setEdits(Arrays.asList(itemEdit1, itemEdit2));
			rows.add(itemRow);
			
		}
		dataElement2.setRows(rows);
		dataElements.add(dataElement2);

		// REASONCODE DATA ELEMENT 3-----------------------------------------

		DataElement dataElement3 = new DataElement();
		dataElement3.setName(IntegrationConstants.DATA_ELEMENT_NAME_REASON_CODES);
		dataElement3.setType(IntegrationConstants.DATA_ELEMENT_TYPE_FORM);

		for (String reasonCode : p21ReturnOrderDataHelper.getReasonCodes()) {
			Row reasonCodeRow = new Row();

			Edit reasonCodeEdit = new Edit();
			reasonCodeEdit.setName(IntegrationConstants.EDIT_NAME_LOST_SALES_ID);
			reasonCodeEdit.setValue(reasonCode);

			reasonCodeRow.setEdits(Arrays.asList(reasonCodeEdit));
			dataElement3.setRows(Collections.singletonList(reasonCodeRow));
		}
		
		dataElements.add(dataElement3);
		// INVOICE DATA ELEMENT 4-----------------------------------------
		DataElement dataElement4 = new DataElement();
		if(p21ReturnOrderDataHelper.getP21OrderItemCustSalesHistory()!=null) {
			dataElement4.setName("TP_CUSTSALESHISTORY.customer_sales_history");
			dataElement4.setType("List");
			
			List<String> values = Arrays.asList(IntegrationConstants.KEY_NAME_CC_INVOICE_NO_DISPLAY, IntegrationConstants.KEY_NAME_ORDER_NO, IntegrationConstants.KEY_NAME_LOCATION_ID);
	        Keys keys = new Keys(values);
		    dataElement4.setKeys(keys);
		    
			Row rowInvoice = new Row();

			Edit editInvoice1 = new Edit();
			editInvoice1.setName(IntegrationConstants.KEY_NAME_ORDER_NO);
			editInvoice1.setValue(p21ReturnOrderDataHelper.getP21OrderItemCustSalesHistory().getOrder_no());

			Edit editInvoice2 = new Edit();
			editInvoice2.setName(IntegrationConstants.KEY_NAME_CC_INVOICE_NO_DISPLAY);
			editInvoice2.setValue(p21ReturnOrderDataHelper.getP21OrderItemCustSalesHistory().getCc_invoice_no_display());

			Edit editInvoice3 = new Edit();
			editInvoice3.setName(IntegrationConstants.KEY_NAME_LOCATION_ID);
			editInvoice3.setValue(p21ReturnOrderDataHelper.getP21OrderItemCustSalesHistory().getLocation_id());

			// Add the Edit objects to the Row object
			rowInvoice.setEdits(Arrays.asList(editInvoice1, editInvoice2, editInvoice3));

					// Add the Row objects to the DataElement objects
			dataElement4.setRows(Collections.singletonList(rowInvoice));	
			dataElements.add(dataElement4);
		}
		
		
		
		// INVOICE DATA ELEMENT 5-----------------------------------------
				DataElement dataElement5 = new DataElement();
				if(p21ReturnOrderDataHelper.getProbDescList()!=null) {
					dataElement5.setName("HDR_NOTE.hdr_note");
					dataElement5.setType("List");
					
					List<String> values = Arrays.asList("note_id");
			        Keys keys = new Keys(values);
				    dataElement5.setKeys(keys);
				   // List<Row>
				    for (P21OrderItemHelper p21OrderItemHelper : p21ReturnOrderDataHelper.getP21OrderItemList()) {
				    	Row rowProbDesc = new Row();

						Edit editInvoice1 = new Edit();
						editInvoice1.setName("note_id");
						editInvoice1.setValue("");

						Edit editInvoice2 = new Edit();
						editInvoice2.setName("topic");
						editInvoice2.setValue("RMA NOTE: ITEM - "+p21OrderItemHelper.getOe_order_item_id());

						Edit editInvoice3 = new Edit();
						editInvoice3.setName("note");
						editInvoice3.setValue(p21OrderItemHelper.getOe_order_item_id()+" -  "+p21OrderItemHelper.getNote());
						
						Edit editInvoice4 = new Edit();
						editInvoice4.setName("notepad_class_desc");
						editInvoice4.setValue("OTHER");


						// Add the Edit objects to the Row object
						rowProbDesc.setEdits(Arrays.asList(editInvoice1, editInvoice2, editInvoice3,editInvoice4));
						// Add the Row objects to the DataElement objects
						dataElement5.setRows(Collections.singletonList(rowProbDesc));
					}
					dataElements.add(dataElement5);
				}
		
		// Add the DataElement objects to the Transaction object
		transaction.setDataElements(dataElements);

		// Add the Transaction object to the TransactionSet object
		transactionSet.setTransactions(Collections.singletonList(transaction));

		return transactionSet;
	}
}
