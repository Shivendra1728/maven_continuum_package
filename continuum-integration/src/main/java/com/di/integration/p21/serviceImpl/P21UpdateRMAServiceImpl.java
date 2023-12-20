package com.di.integration.p21.serviceImpl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21UpdateRMAService;
import com.di.integration.p21.transaction.DataElement;
import com.di.integration.p21.transaction.Edit;
import com.di.integration.p21.transaction.Row;
import com.di.integration.p21.transaction.Transaction;
import com.di.integration.p21.transaction.TransactionSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Service
public class P21UpdateRMAServiceImpl implements P21UpdateRMAService{
	
	private static final Logger logger = LoggerFactory.getLogger(P21OrderServiceImpl.class);
	
	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	String RMA_UPDATE_RESTOCKING_API;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	
	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public String updateRMARestocking(String rmaNumber, Double totalRestocking) throws Exception {
		

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		
		totalRestocking = -totalRestocking;
		String updateRestockingXml = prepareRestockingXML(rmaNumber, totalRestocking, masterTenant.getRestockingItemId());
		
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request = new HttpPost(masterTenant.getSubdomain()+RMA_UPDATE_RESTOCKING_API);

		// Set request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity = new StringEntity(updateRestockingXml);
		request.setEntity(entity);
		CloseableHttpResponse response = httpClient.execute(request);
		return EntityUtils.toString(response.getEntity());

		
	}
	
	@Override
	public String updateAmount(String rmaNo, List<ReturnOrderItem> returnOrderItems) throws Exception {
		
		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
				
		String updateAmountXml = prepareXml(rmaNo, returnOrderItems);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request = new HttpPost(masterTenant.getSubdomain()+RMA_UPDATE_RESTOCKING_API);

		// Set request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity = new StringEntity(updateAmountXml);
		request.setEntity(entity);
		logger.info("Updating Amount to ERP Body : "+updateAmountXml);
		CloseableHttpResponse response = httpClient.execute(request);
		logger.info("Amount Uddated to ERP");
		return EntityUtils.toString(response.getEntity());
	}
	
	public String prepareRestockingXML(String rmaNo, Double totalRestocking, String itemName) throws JsonProcessingException {
		TransactionSet transactionSet = new TransactionSet();
		transactionSet.setIgnoreDisabled(true);
		transactionSet.setName(IntegrationConstants.RMA);
		Transaction transaction = new Transaction();
		
		List<DataElement> dataElements= new ArrayList<>();

		// ORDER HEADER DATA ELEMENT 1-----------------------------------------
		DataElement dataElement1 = new DataElement();
		dataElement1.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER);
		dataElement1.setType(IntegrationConstants.DATA_ELEMENT_TYPE_FORM);
		
		List<Row> rowList = new ArrayList<Row>();
		Row row1 = new Row();
		List<Edit> editList = new ArrayList<Edit>();
		Edit edit1 = new Edit();
		edit1.setName("order_no");
		edit1.setValue(rmaNo);
		editList.add(edit1);
		row1.setEdits(editList);
		rowList.add(row1);
		dataElement1.setRows(rowList);
		dataElements.add(dataElement1);
		
		DataElement dataElement2 = new DataElement();
		dataElement2.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER_ITEMS);
		dataElement2.setType(IntegrationConstants.DATA_ELEMENT_TYPE_LIST);
		
		List<Row> rowList1 = new ArrayList<Row>();
		
		Row row2 = new Row();
		List<Edit> editList1 = new ArrayList<Edit>();
		
		Edit edit2 = new Edit();
		edit2.setName("oe_order_item_id");
		edit2.setValue(itemName);
		
		Edit edit4 = new Edit();
		edit4.setName("unit_quantity");
		edit4.setValue("1");
			
		Edit edit3 = new Edit();
		edit3.setName("unit_price");
		edit3.setValue(String.valueOf(totalRestocking));
			
		editList1.add(edit2);
		editList1.add(edit4);
		editList1.add(edit3);
		
		row2.setEdits(editList1);
		rowList1.add(row2);
		
		dataElement2.setRows(rowList1);
		dataElements.add(dataElement2);
		
		transaction.setDataElements(dataElements);
		transactionSet.setTransactions(Collections.singletonList(transaction));
		
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// xmlMapper.disable(MapperFeature.USE_STD_BEAN_NAMING);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// xmlMapper.setDefaultUseWrapper(false);
		String xml = xmlMapper.writeValueAsString(transactionSet);
		
		xml = xml.replaceAll("wstxns2:", "");
		xml = xml.replaceAll("xmlns:wstxns2", "xmlns:a");

		xml = xml.replaceAll("wstxns1:", "");
		xml = xml.replaceAll("xmlns:wstxns1", "xmlns:a");

		xml = xml.replaceAll("wstxns3:", "");
		xml = xml.replaceAll("xmlns:wstxns3", "xmlns:a");
		
		xml = xml.replaceAll("wstxns4:", "");
		xml = xml.replaceAll("xmlns:wstxns4", "xmlns:a");
		logger.info("Updated restocking XML");
		logger.info(xml);
		return xml;
	}
	
	public String prepareXml(String rmaNo, List<ReturnOrderItem> returnOrderItems) throws JsonProcessingException{
		TransactionSet transactionSet = new TransactionSet();
		transactionSet.setIgnoreDisabled(true);
		transactionSet.setName(IntegrationConstants.RMA);
		Transaction transaction = new Transaction();


		List<DataElement> dataElements= new ArrayList<>();

		// ORDER HEADER DATA ELEMENT 1-----------------------------------------
		DataElement dataElement1 = new DataElement();
		dataElement1.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER);
		dataElement1.setType(IntegrationConstants.DATA_ELEMENT_TYPE_FORM);
		
		List<Row> rowList = new ArrayList<Row>();
		Row row1 = new Row();
		List<Edit> editList = new ArrayList<Edit>();
		Edit edit1 = new Edit();
		edit1.setName("order_no");
		edit1.setValue(rmaNo);
		editList.add(edit1);
		row1.setEdits(editList);
		rowList.add(row1);
		dataElement1.setRows(rowList);
		dataElements.add(dataElement1);
		
		DataElement dataElement2 = new DataElement();
		dataElement2.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER_ITEMS);
		dataElement2.setType(IntegrationConstants.DATA_ELEMENT_TYPE_LIST);
		
		List<Row> rowList1 = new ArrayList<Row>();
		for(ReturnOrderItem returnOrderItem : returnOrderItems) {
			Row row2 = new Row();
			List<Edit> editList1 = new ArrayList<Edit>();
			
			Edit edit2 = new Edit();
			edit2.setName("oe_order_item_id");
			edit2.setValue(returnOrderItem.getItemName());
				
			Edit edit3 = new Edit();
			edit3.setName("unit_price");
			edit3.setValue(String.valueOf(returnOrderItem.getAmount()));
				
			editList1.add(edit2);
			editList1.add(edit3);
			
			row2.setEdits(editList1);
			rowList1.add(row2);
		}
		
		dataElement2.setRows(rowList1);
		dataElements.add(dataElement2);
		
		transaction.setDataElements(dataElements);
		transactionSet.setTransactions(Collections.singletonList(transaction));
		
		XmlMapper xmlMapper = new XmlMapper();
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
		// xmlMapper.disable(MapperFeature.USE_STD_BEAN_NAMING);
		xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// xmlMapper.setDefaultUseWrapper(false);
		String xml = xmlMapper.writeValueAsString(transactionSet);
		
		xml = xml.replaceAll("wstxns2:", "");
		xml = xml.replaceAll("xmlns:wstxns2", "xmlns:a");

		xml = xml.replaceAll("wstxns1:", "");
		xml = xml.replaceAll("xmlns:wstxns1", "xmlns:a");

		xml = xml.replaceAll("wstxns3:", "");
		xml = xml.replaceAll("xmlns:wstxns3", "xmlns:a");
		
		xml = xml.replaceAll("wstxns4:", "");
		xml = xml.replaceAll("xmlns:wstxns4", "xmlns:a");
		
		return xml;
		
	}

}
