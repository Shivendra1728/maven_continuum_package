package com.di.integration.p21.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21SKUService;
import com.di.integration.p21.transaction.DataElement;
import com.di.integration.p21.transaction.Edit;
import com.di.integration.p21.transaction.P21ReturnOrderMarshller;
import com.di.integration.p21.transaction.Row;
import com.di.integration.p21.transaction.Transaction;
import com.di.integration.p21.transaction.TransactionSet;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/*
 * @author SS
 *
 */

@Service
public class P21SKUServiceImpl implements P21SKUService {
	private static final Logger logger = LoggerFactory.getLogger(P21OrderLineServiceImpl.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;
	
	@Autowired
	P21ReturnOrderMarshller p21ReturnOrderMarshller;
	
	@Autowired
	P21OrderLineServiceImpl p21orderLineServiceImpl;
	
	@Value(IntegrationConstants.ERP_RMA_CREATE_API)
	String RMA_CREATE_API;
	
	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	private String rmaGetEndPoint;

	@Value(IntegrationConstants.ERP_RMA_WINDOW_ENDPOINT)
	private String rmaWindowEndpoint;
	

	@Override
	public String deleteSKU(String itemId, String rmaNo, MasterTenant masterTenantObject) throws Exception {

		MasterTenant masterTenant;

		if (masterTenantObject == null) {
			String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
			masterTenant = masterTenantRepository.findByDbName(tenantId);
		} else {
			masterTenant = masterTenantObject;
		}
		// Token for tenant/ERP
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("THE TOKEN IS:" + token);
		// Hitting URLS one by one
		// Every API will have content type and accept as application/json.
		// Lets make two global variable to be used anywhere in code ..

		String windowId = null;
		String sessionId = null;
		int rowNumber = 0;
		String childWindowId = null;

		// First
		// API---------------------------------------------------------------------------------------------------------------------------------
		String openSession = masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/";
		try {
			logger.info("1) Open Session for delete line");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(openSession);
			HttpPost httpPost = new HttpPost(uriBuilder.build());

			httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String jsonResponse = EntityUtils.toString(entity);
				JSONObject jsonObject = new JSONObject(jsonResponse);

				logger.info("First Json" + jsonResponse);
				sessionId = jsonObject.getString("Id");
				logger.info("SessionId: " + sessionId);
			} else {
				logger.info("Response not generated from 1st API.");
			}

		} catch (Exception e) {
			logger.error("Error Creating Session :: " + e.getMessage());
		}

		// Store session id to use in the end

		// Second
		// Api--------------------------------------------------------------------------------------------------------------------------------------
		String openWindow = masterTenant.getSubdomain() + rmaWindowEndpoint;
		try {
			logger.info("2) Open Window for delete line");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(openWindow);
			HttpPost httpPost = new HttpPost(uriBuilder.build());

			httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");

			String requestBodyForWindow = "\"RMA\"";
			StringEntity stringEntity = new StringEntity(requestBodyForWindow);
			httpPost.setEntity(stringEntity);

			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String jsonResponse = EntityUtils.toString(entity);
				JSONObject jsonObject = new JSONObject(jsonResponse);

				windowId = jsonObject.getString("Result");
				logger.info("WindowId: " + windowId);
			} else {
				logger.info("Response not generated from 2nd API.");
			}

		} catch (Exception e) {
			logger.error("Error Creating Window :: " + e.getMessage());
		}

		// Store window id to pass in most apis
		// Third API
		// ------------------------------------------------------------------------------------------------------------------------------

		String changeDataForAField = masterTenant.getSubdomain() + rmaWindowEndpoint + windowId
				+ "/elements/changedata?datawindowName=order&fieldName=order_no";

		try {
			logger.info("3) Change data for a field");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(changeDataForAField);
			HttpPut httpPut = new HttpPut(uriBuilder.build());

			httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-Type", "application/json");

			String requestBody = "\"" + rmaNo + "\"";
			StringEntity entity3 = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
			httpPut.setEntity(entity3);

			CloseableHttpResponse response = httpClient.execute(httpPut);
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Third API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in third api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Changing data for a field :: " + e.getMessage());
		}

		// If 200 move ahead

		// Fourth API
		// ---------------------------------------------------------------------------------------------------------------------

		String rmaDetails = masterTenant.getSubdomain() + rmaGetEndPoint + "/get";
		try {
			logger.info("4) Getting RMA Details");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			logger.info("Get RMA Details API-" + rmaDetails);

			URIBuilder uriBuilder = new URIBuilder(rmaDetails);
			HttpPost httpPost = new HttpPost(uriBuilder.build());

			httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setHeader("Accept", "application/json");

			int rmaNumber = Integer.parseInt(rmaNo);
			String jsonPayload = String.format("{\n" + "    \"ServiceName\":\"RMA\",\n"
					+ "    \"TransactionStates\":[{\n" + "        \"DataElementName\":\"TABPAGE_1.order\",\n"
					+ "        \"Keys\":[{\n" + "            \"Name\":\"order_no\",\n" + "            \"Value\":%d\n"
					+ "        }]\n" + "    }],\n" + "    \"UseCodeValues\":true\n" + "}", rmaNumber);
			httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

			CloseableHttpResponse response = httpClient.execute(httpPost);

			String responseBody = EntityUtils.toString(response.getEntity());
			logger.info("Response from RMA detail: " + responseBody);
			JsonNode rootNode = objectMapper.readTree(responseBody);

			JsonNode itemsNode = rootNode.path("Transactions").get(0).path("DataElements").get(41).path("Rows");
			List<String> itemIdsList = new ArrayList<>();
			for (JsonNode item : itemsNode) {
				// Picking up item id/item ids from rma

				String itemIdInXML = item.path("Edits").get(0).path("Value").asText();
				itemIdsList.add(itemIdInXML);
				logger.info("Item ID: " + itemIdInXML);
				if (itemIdInXML.equals(itemId)) {

					rowNumber = itemIdsList.indexOf(itemId) + 1;
					logger.info("Row Number to target is: " + rowNumber);

					break;
				} else {
					logger.info("I can't identify this item id");
					continue;

				}
			}

		} catch (Exception e) {
			logger.error("Error Getting RMA Details :: " + e.getMessage());
		}
		if (rowNumber == 0) {
			return "I couldn't identify the row in RMA for this item";
		}

		// Give a request body and then call this to get position after looping items
		// and pick up a row number
		// and then carry forward

		// Fifth API
		// --------------------------------------------------------------------------------------------------------------------------------

		String setFocusOnAField = masterTenant.getSubdomain() + rmaWindowEndpoint + windowId
				+ "/elements/focus?datawindowName=items&fieldName=oe_order_item_id&row=" + rowNumber;
		try {
			logger.info("5) Set Focus On A Field");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(setFocusOnAField);
			logger.info("Set focus on field URL made-" + setFocusOnAField);
			HttpPost httpPost = new HttpPost(uriBuilder.build());

			httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			logger.info("Response of focus ::" + response);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Fifth API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in Fifth api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Set Focus On A field :: " + e.getMessage());
		}

		// if 200 move ahead
		// Sixth API
		// ----------------------------------------------------------------------------------------------------------------
		String runToolUrl = String.format(
				"%s/uiserver0/ui/full/v1/window/%s/elements/tools/run?dwName=%s&toolName=%s&dwElementName=%s&row=%s",
				masterTenant.getSubdomain(), windowId, "items", "m_deletelline", "items", rowNumber);
		try {
			logger.info("6) Run Tool on Window");

			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
				URIBuilder uriBuilder = new URIBuilder(runToolUrl);
				HttpPut httpPut = new HttpPut(uriBuilder.build());

				httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				httpPut.addHeader(HttpHeaders.ACCEPT, "application/json");
				httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

				try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
					HttpEntity entity = response.getEntity();

					if (HttpStatus.valueOf(response.getStatusLine().getStatusCode()).is2xxSuccessful()) {
						String runToolOnWindowResponseBody = EntityUtils.toString(entity);
						logger.info("Run Tool on Window " + runToolOnWindowResponseBody);

						ObjectMapper objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(runToolOnWindowResponseBody);

						JsonNode eventsNode = rootNode.path("Events");
						if (eventsNode.isArray() && eventsNode.size() > 0) {
							JsonNode eventDataNode = eventsNode.get(0).path("EventData");
							if (eventDataNode.has("windowid")) {
								childWindowId = eventDataNode.get("windowid").asText();
								logger.info("Child Window Id: " + childWindowId);
							}
						}

					} else {
						logger.error("Error: " + response.getStatusLine().getStatusCode());
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error Running Tool At The Row :: " + ex.getMessage());
			logger.info("Finding Child Window ID using regular expression.");

			if (ex instanceof HttpClientErrorException || ex instanceof HttpServerErrorException) {
				// Handle the exception response
				String responseBody = ex instanceof HttpClientErrorException
						? ((HttpClientErrorException) ex).getResponseBodyAsString()
						: ((HttpServerErrorException) ex).getResponseBodyAsString();

				logger.error("Error Response Body: " + responseBody);

				// You can extract information from the error response if needed
				Pattern pattern = Pattern
						.compile("<ErrorMessage>[^<]*window\\s([^\\s]+)[^<]*blocks[^<]*</ErrorMessage>");
				Matcher matcher = pattern.matcher(responseBody);
				if (matcher.find()) {
					childWindowId = matcher.group(1);
					logger.info("THIS IS CHILD WINDOW ID :: " + childWindowId);
				} else {
					logger.error("CHILD WINDOW ID COULDN'T BE PARSED..");
				}
			} else {
				logger.error("Unexpected Exception: " + ex.getClass().getName() + " - " + ex.getMessage());
			}
		}

		// value for row put dynamic
		// Parse the 500 response to get child window id and pass on to next one.

		// Seventh
		// API------------------------------------------------------------------------------------------------------------------------------------------------

		String childWindowActive = masterTenant.getSubdomain() + rmaWindowEndpoint + childWindowId + "/active";

		try {
			logger.info("7) Child Window Active");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(childWindowActive);
			HttpGet httpGet = new HttpGet(uriBuilder.build());

			httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpGet.setHeader("Accept", "application/json");
			httpGet.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Seventh API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in Seventh api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Child Window Is Not Active :: " + e.getMessage());
		}

		// If 200 that means child window is active and you can delete and save session
		// Eighth
		// API------------------------------------------------------------------------------------------------------------------------------------------

		String toolOfChildWindow = masterTenant.getSubdomain() + rmaWindowEndpoint + childWindowId + "/tools/cb_1";

		try {
			logger.info("8) Tool Of Child window ");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(toolOfChildWindow);
			HttpPut httpPut = new HttpPut(uriBuilder.build());

			httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpPut);
			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Eighth API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in Eighth api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Running Tool Of Child Window :: " + e.getMessage());
		}

		// If 200 that means we clicked Yes option if you want to click No then choose
		// cb_2
		// Ninth
		// API-----------------------------------------------------------------------------------------------------------------------------------------

		String saveWindow = masterTenant.getSubdomain() + rmaWindowEndpoint + windowId + "/save";
		try {
			logger.info("9) Save window ");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(saveWindow);
			HttpPost httpPost = new HttpPost(uriBuilder.build());

			httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Ninth API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in Ninth api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Saving Window :: " + e.getMessage());
		}

		// if 200 then success window saved , line deleted.

		// Tenth
		// API----------------------------------------------------------------------------------------------------------------------

		String closeMainWindow = masterTenant.getSubdomain() + rmaWindowEndpoint + windowId;

		try {
			logger.info("10) Close Main window ");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(closeMainWindow);
			HttpDelete httpDelete = new HttpDelete(uriBuilder.build());

			httpDelete.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpDelete.setHeader("Accept", "application/json");
			httpDelete.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpDelete);

			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Tenth API ran successfully.");
			} else {
				logger.error("Error: Unexpected status code in Tenth api - " + statusCode);
			}

		} catch (Exception e) {
			logger.error("Error Closing Main Window :: " + e.getMessage());
		}

		// if 200 that means window has been closed now we end the session

		// Eleventh
		// API---------------------------------------------------------------------------------------------------------

		String endSession = masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions?Id=" + sessionId;

		try {
			logger.info("11) End session ");

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			URIBuilder uriBuilder = new URIBuilder(endSession);
			HttpDelete httpDelete = new HttpDelete(uriBuilder.build());

			httpDelete.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			httpDelete.setHeader("Accept", "application/json");
			httpDelete.setHeader("Content-Type", "application/json");

			CloseableHttpResponse response = httpClient.execute(httpDelete);

			HttpEntity entity = response.getEntity();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				logger.info("Eleventh API ran successfully. Session End - RMA Delete line procedure completed.");
				return "Process Complete Line Item from ERP deleted";
			} else {
				logger.error("Error: Unexpected status code in Tenth api - " + statusCode);
				return "Process complete session was already active, Still Line Item from ERP deleted";
			}

		} catch (Exception e) {
			logger.error("Error Closing Session :: " + e.getMessage());
		}
		return "Session End - Item Deleted";

		// if null response that means everything worked ...Chill.

	}
	
	@Override
	public String addSKU(String rmaNo,List<ReturnOrderItemDTO> returnOrderItemDTOList,  MasterTenant masterTenantObject)throws Exception{
		
		MasterTenant masterTenant;

		if (masterTenantObject == null) {
			String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
			masterTenant = masterTenantRepository.findByDbName(tenantId);
		} else {
			masterTenant = masterTenantObject;
		}
		// Token for tenant/ERP
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("THE TOKEN IS:" + token);
		
		String xmlPayload=prepareAddItemXml(rmaNo , returnOrderItemDTOList);
		logger.info("returnOrderXmlPayload {}", xmlPayload);

		try {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request = new HttpPost(masterTenant.getSubdomain() + RMA_CREATE_API);

		// Set request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		logger.info("#### TOKEN #### {}", token);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity = new StringEntity(xmlPayload);
		request.setEntity(entity);
		CloseableHttpResponse response = httpClient.execute(request);
		String responseBody = EntityUtils.toString(response.getEntity());

		logger.info("#### RMA LINE RESPONSE #### {}", responseBody);
		return "Line Item Added";

		}catch(Exception e) {
			return "Failed to add line item";
		}
	}

	
	private String prepareAddItemXml(String rmaNo , List<ReturnOrderItemDTO> returnOrderItemDTOList)throws Exception {
		TransactionSet transactionSet = new TransactionSet();
	    transactionSet.setIgnoreDisabled(true);
	    transactionSet.setName(IntegrationConstants.RMA);
	    Transaction transaction = new Transaction();
	    
	    List<DataElement> dataElements = new ArrayList<>();

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
	    
	    // ORDER ITEMS DATA ELEMENT 2-----------------------------------------
	    DataElement dataElement2 = new DataElement();
	    dataElement2.setName(IntegrationConstants.DATA_ELEMENT_NAME_ORDER_ITEMS);
	    dataElement2.setType(IntegrationConstants.DATA_ELEMENT_TYPE_LIST);
	    
	    List<Row> rowList1 = new ArrayList<Row>();
	    
	    for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderItemDTOList) {
	        Row row = new Row();
	        List<Edit> editList1 = new ArrayList<Edit>();
	        
	        Edit edit2 = new Edit();
	        edit2.setName("oe_order_item_id");
	        edit2.setValue(returnOrderItemDTO.getItemName());
	        
	        Edit edit4 = new Edit();
	        edit4.setName("unit_quantity");
	        edit4.setValue(String.valueOf(returnOrderItemDTO.getQuanity()));
	        
	        editList1.add(edit2);
	        editList1.add(edit4);
	        
	        row.setEdits(editList1);
	        rowList1.add(row);
	    }
	    
	    dataElement2.setRows(rowList1);
	    dataElements.add(dataElement2);
	    
	    transaction.setDataElements(dataElements);
	    transactionSet.setTransactions(Collections.singletonList(transaction));
	    
	    XmlMapper xmlMapper = new XmlMapper();
	    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
	    xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
