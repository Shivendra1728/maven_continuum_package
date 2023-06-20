package com.di.integration.p21.transaction;

import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.JAXBException;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class P21ReturnOrderMarshller {

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
		System.out.println(xml);
		return xml;
	}

	private TransactionSet prepareXMl(P21ReturnOrderDataHelper p21ReturnOrderDataHelper) {
		TransactionSet transactionSet = new TransactionSet();
		transactionSet.setIgnoreDisabled(true);
		transactionSet.setName("RMA");
		Transaction transaction = new Transaction();

		// ORDER HEADER DATA ELEMENT 1-----------------------------------------
		DataElement dataElement1 = new DataElement();
		dataElement1.setName("TABPAGE_1.order");
		dataElement1.setType("Form");

		Row row1 = new Row();

		Edit edit1 = new Edit();
		edit1.setName("company_id");
		edit1.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getCompany_id());

		Edit edit2 = new Edit();
		edit2.setName("customer_id");
		edit2.setValue(p21ReturnOrderDataHelper.getP21OrderHeader().getCustomer_id());

		Edit edit3 = new Edit();
		edit3.setName("sales_loc_id");
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

		// Add the Edit objects to the Row object
		row1.setEdits(Arrays.asList(edit1, edit2, edit3, edit4, edit5, edit6, edit7));

		// Add the Row objects to the DataElement objects
		dataElement1.setRows(Collections.singletonList(row1));

		// ORDERITEM DATA ELEMENT 1-----------------------------------------

		DataElement dataElement2 = new DataElement();
		dataElement2.setName("TP_ITEMS.items");
		dataElement2.setType("List");

		for (P21OrderItemHelper p21OrderItemHelper : p21ReturnOrderDataHelper.getP21OrderItemList()) {
			Row itemRow = new Row();

			// Create the Edit objects
			Edit itemEdit1 = new Edit();
			itemEdit1.setName("oe_order_item_id");
			itemEdit1.setValue(p21OrderItemHelper.getOe_order_item_id());

			Edit itemEdit2 = new Edit();
			itemEdit2.setName("unit_quantity");
			itemEdit2.setValue(p21OrderItemHelper.getUnit_quantity());

			itemRow.setEdits(Arrays.asList(itemEdit1, itemEdit2));
			dataElement2.setRows(Collections.singletonList(itemRow));
		}

		// REASONCODE DATA ELEMENT 3-----------------------------------------

		DataElement dataElement3 = new DataElement();
		dataElement3.setName("REASONCODESHDR.reasoncodeshdr");
		dataElement3.setType("Form");

		for (String reasonCode : p21ReturnOrderDataHelper.getReasonCodes()) {
			Row reasonCodeRow = new Row();

			Edit reasonCodeEdit = new Edit();
			reasonCodeEdit.setName("lost_sales_id");
			reasonCodeEdit.setValue(reasonCode);

			reasonCodeRow.setEdits(Arrays.asList(reasonCodeEdit));
			dataElement3.setRows(Collections.singletonList(reasonCodeRow));
		}

		// Add the DataElement objects to the Transaction object
		transaction.setDataElements(Arrays.asList(dataElement1, dataElement2, dataElement3));

		// Add the Transaction object to the TransactionSet object
		transactionSet.setTransactions(Collections.singletonList(transaction));

		return transactionSet;
	}
}
