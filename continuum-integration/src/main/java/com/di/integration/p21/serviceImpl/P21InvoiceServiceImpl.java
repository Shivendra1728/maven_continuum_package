package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21InvoiceMapper;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21InvoiceService;

@Service
public class P21InvoiceServiceImpl implements P21InvoiceService {
	private static final Logger logger = LoggerFactory.getLogger(P21InvoiceServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_INVOICE_VIEW)
	String DATA_API_INVOICE_VIEW;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_VIEW)
	String DATA_API_ORDER_VIEW;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_LINE)
	String DATA_API_ORDER_LINE;

	@Value(IntegrationConstants.ERP_ORDER_LINE_SELECT_FIELDS)
	String ORDER_LINE_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_SELECT_FIELDS)
	String ORDER_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	P21OrderLineServiceImpl p21OrderLineServiceImpl;

	@Autowired
	P21OrderLineItemMapper p21orderLineItemMapper;

	@Autowired
	P21InvoiceMapper p21InvoiceMapper;

	@Autowired
	P21OrderMapper p21OrderMapper;

	@Autowired
	StoreDTO storeDTO;


	LocalDate localDate;

	public String getInvoiceLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fulluri = prepareInvoiceLineURI(orderSearchParameters, totalItem);
		logger.info("getInvoiceLineData URI: " + fulluri);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		RequestEntity<Void> requestMapping = new RequestEntity<>(headers, HttpMethod.GET, fulluri);
		ResponseEntity<String> response = restTemplate.exchange(requestMapping, String.class);
		return response.getBody();
	}

	private URI prepareInvoiceLineURI(OrderSearchParameters orderSearchParameters, int totalItem)
			throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			System.out.println("Invoice no" + orderSearchParameters.getInvoiceNo());
			filter.append(IntegrationConstants.INVOICE_NO).append(" ")
	        .append(IntegrationConstants.CONDITION_EQ).append(" '")
	        .append(orderSearchParameters.getInvoiceNo()).append("'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getOrderNo())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}

			filter.append(IntegrationConstants.ORDER_NO).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(orderSearchParameters.getOrderNo()).append("'");
		}
		
		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			// filter.append("order_no eq '" + orderSearchParameters.getOrderNo() + "'");

			filter.append(IntegrationConstants.BILL_TO_POSTAL_CODE).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(orderSearchParameters.getZipcode()).append("'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append(IntegrationConstants.CUSTOMER_ID).append(" ")
	        .append(IntegrationConstants.CONDITION_EQ).append(" '")
	        .append(orderSearchParameters.getCustomerId()).append("'");
			
		}
		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter=" + encodedFilter;
			if (totalItem == 1) {
				query = query + "&$top=1";
			}
			URI uri = new URI(DATA_API_BASE_URL + DATA_API_INVOICE_VIEW);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			return fullURI;
		} catch (Exception e) {

			logger.error("An error occurred while preparing the order line URI: {}", e.getMessage());

		}
		return null;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}
	
	@Override
	public boolean linkInvoice(String rmaNo) throws Exception {
		boolean b=false;
		
		URI sessionEnd= new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/common/v1/sessions/"); 
		URI sessionEndFullURI= sessionEnd.resolve(sessionEnd.getRawPath());
		
		URI sessionCreate = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/common/v1/sessions/");
		URI sessionCreatefullURI = sessionCreate.resolve(sessionCreate.getRawPath());
		
		URI openWindow = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/window/");
		URI openWindowfullURI = openWindow.resolve(openWindow.getRawPath());
		
		URI windowMetaData = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/transaction/metadata/RMA");
		URI windowMetaDatafullURI = windowMetaData.resolve(windowMetaData.getRawPath());
		
		URI windowList = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/transaction/services?type=Window");
		URI windowListfullURI = windowList.resolve(windowList.getRawPath());
		
		System.out.println(windowListfullURI.toString());
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		logger.info("create session URI:" + sessionCreate);
		// Set the Accept header to receive JSON response
		//headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("Accept","application/json");
		headers.add("Content-Type", "application/json");
		//headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$ 
		// Create the request entity with headers
		
		try {
			//Check and terminate if there is any session exists
			RequestEntity<Void> requestEntitySessionEnd = new RequestEntity<>(headers, HttpMethod.DELETE, sessionEndFullURI);
			// Make the API call
			ResponseEntity<String> responseSessionEnd = restTemplate.exchange(requestEntitySessionEnd, String.class);
			responseSessionEnd.getBody();
			 
		}catch (Exception e) {
			logger.error("There is no session exists:" +e.getMessage());
		}
		
		 RequestEntity<Void> requestEntitySessionCreate = new RequestEntity<>(headers, HttpMethod.POST, sessionCreatefullURI);
		 logger.info("Session Create URI:" + sessionCreatefullURI);
			// Make the API call
			ResponseEntity<String> sessionCreateResponse = restTemplate.exchange(requestEntitySessionCreate, String.class);
			sessionCreateResponse.getBody();
			
			// RequestEntity<Void> openWindowrequestEntity = new RequestEntity<>(headers, HttpMethod.POST, openWindowfullURI);
			 logger.info("Open Window URI:" + openWindowfullURI);
				// Make the API call
				//ResponseEntity<String> openWindowResponse = restTemplate.exchange(openWindowrequestEntity, String.class);
				/*
				 * ResponseEntity<String> openWindowResponse =
				 * restTemplate.exchange(openWindowfullURI, HttpMethod.POST, new
				 * HttpEntity<String>("RMA",headers), String.class);
				 * openWindowResponse.getBody();
				 */

				//HttpHeaders headers1 = new HttpHeaders();
				//headers1.setBearerAuth(p21TokenServiceImpl.getToken());
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				HttpEntity<String> httpEntity = new HttpEntity<>("\"RMA\"", headers);

				ResponseEntity<String> openWindowResponse = restTemplate.postForEntity(openWindowfullURI, httpEntity, String.class);
				openWindowResponse.getBody();
				JSONObject jsonObject = new JSONObject(openWindowResponse.getBody());
				String windowId = jsonObject.getString("Result");
				//ResponseEntity<String> windowMetaDataResponse = restTemplate.getForEntity(windowMetaDatafullURI, hedaers, String.class);
				openWindowResponse.getBody();
				logger.info("windowMetaDataResponse URI:" + windowMetaDatafullURI);
				 ResponseEntity<Object> windowMetaDataResponse = restTemplate.exchange(
						 windowMetaDatafullURI, HttpMethod.GET, new HttpEntity<Object>(headers),
			                Object.class);
				 
				 logger.info("windowListfullURI:" + windowListfullURI);
				 ResponseEntity<Object>  windowListResponse = restTemplate.exchange(
						 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/transaction/services?type={Window}", HttpMethod.GET, new HttpEntity<Object>(headers),
			                Object.class,"Window");
				 windowListResponse.getBody();
				 
					/*
					 * restTemplate.exchange(
					 * "http://my-rest-url.org/rest/account/{account}?name={name}", HttpMethod.GET,
					 * httpEntity, Object.class, "my-account", "my-name" );
					 */
				 
				// URI changeDataForAField = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/window/"+windowId+"/elements/changedata?datawindowName=order&fieldName=order_no");
				//	URI changeDataForAFieldFullURI = changeDataForAField.resolve(changeDataForAField.getRawPath());
					 logger.info("changeDataForAFieldFullURI:" );
					 
					 ResponseEntity<Object>  changeDataForAFieldResponse = restTemplate.exchange(
							 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={order}&fieldName={order_no}", HttpMethod.PUT, new HttpEntity<Object>("\""+rmaNo+"\"",headers),
				                Object.class,windowId,"order","order_no");
					 changeDataForAFieldResponse.getBody();
					 
					
					// URI selectPagOfWindow = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/window/"+windowId+"/elements/select?pageName=tabpage_saleshistory");
					//	URI selectPagOfWindowFullURI = selectPagOfWindow.resolve(selectPagOfWindow.getRawPath());
						 logger.info("selectPagOfWindowFullURI:" );
						 ResponseEntity<Object>  selectPagOfWindowResponse = restTemplate.exchange(
								 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/select?pageName={tabpage_saleshistory}", HttpMethod.POST, new HttpEntity<Object>("\"423651\"",headers),
					                Object.class,windowId,"tabpage_saleshistory");
						 selectPagOfWindowResponse.getBody();
						 
						// URI getToolsOfWindowTab = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/transaction/services?type=Window");
						//	URI getToolsOfWindowTabfullURI = getToolsOfWindowTab.resolve(getToolsOfWindowTab.getRawPath());
							
							
							//URI toolsOfWindowField = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/transaction/services?type=Window");
						//	URI toolsOfWindowFieldfullURI = toolsOfWindowField.resolve(toolsOfWindowField.getRawPath());
							
						//	URI currentRowForDataWindow = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/transaction/services?type=Window");
							//URI currentRowForDataWindowfullURI = currentRowForDataWindow.resolve(currentRowForDataWindow.getRawPath());
							
							URI activeWindowDefinition = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/full/v1/window/"+windowId+"/active");
							URI activeWindowDefinitionfullURI = activeWindowDefinition.resolve(activeWindowDefinition.getRawPath());
							
							URI getActiveWindows = new URI("https://apiplay.labdepotinc.com" + "/uiserver0/ui/common/v1/sessions/window/all");
							URI getActiveWindowsfullURI = getActiveWindows.resolve(getActiveWindows.getRawPath());
							
						 
						 
							 logger.info("getToolsOfWindowTab:" );
							 ResponseEntity<Object>  getToolsOfWindowTabResponse = restTemplate.exchange(
									 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/tools?datawindowName={tabpage_saleshistory}", HttpMethod.GET, new HttpEntity<Object>(headers),
						                Object.class,windowId,"tabpage_saleshistory");
							 getToolsOfWindowTabResponse.getBody();
							 
							 logger.info("toolsOfWindowFieldfullURI:" );
							 ResponseEntity<Object>  toolsOfWindowFieldResponse = restTemplate.exchange(
									 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/tools?datawindowName=tabpage_saleshistory&fieldName=ship2_name", HttpMethod.GET, new HttpEntity<Object>(headers),
						                Object.class,windowId,"tabpage_saleshistory","ship2_name");
							 toolsOfWindowFieldResponse.getBody();
							 
							 logger.info("currentRowForDataWindow" );
							 ResponseEntity<Object>  currentRowForDataWindowResponse = restTemplate.exchange(
									 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/activerow?datawindowName={tabpage_saleshistory}", HttpMethod.GET, new HttpEntity<Object>(headers),
						                Object.class,windowId,"tabpage_saleshistory");
							 currentRowForDataWindowResponse.getBody();
							 
							 logger.info("activeWindowDefinition" );
							 ResponseEntity<Object> activeWindowDefinitionResponse = restTemplate.exchange(
									 activeWindowDefinitionfullURI, HttpMethod.GET, new HttpEntity<Object>(headers),
						                Object.class);
							 activeWindowDefinitionResponse.getBody();
							 
							 logger.info("getActiveWindows" );
							 ResponseEntity<Object>  getActiveWindowsResponse = restTemplate.exchange(
									 getActiveWindowsfullURI, HttpMethod.GET, new HttpEntity<Object>(headers),
						                Object.class);
							 getActiveWindowsResponse.getBody();
							 
							 try {
								 logger.info("Set Focus on specified Field:" );
								 ResponseEntity<Object>  setFocusOnSpecifiedField = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/focus?datawindowName={tabpage_saleshistory}&fieldName={invoice_no}&row={rowNo}", HttpMethod.POST, new HttpEntity<Object>(headers),
							                Object.class,windowId,"tabpage_saleshistory","invoice_no","3");
								 setFocusOnSpecifiedField.getBody();
								 
								 
							 }catch (Exception e) {
								 logger.error("Error Set Focus on specified field:: "+e.getMessage());
							}
							 
							 try {
								 logger.info("Run Tool on Window" );
								 ResponseEntity<Object>  runToolOnWindowResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/tools/run?dwName={tabpage_saleshistory}&toolName={m_linktothisrmaline}&dwElementName={tabpage_saleshistory}&row={rowNo}", HttpMethod.PUT, new HttpEntity<Object>(headers),
							                Object.class,windowId,"tabpage_saleshistory","m_linktothisrmaline","tabpage_saleshistory","3");
								 runToolOnWindowResponse.getBody();
								 
								 
							 }catch (Exception e) {
								 logger.error("Error Run Tool on Window::"+e.getMessage());
							}
							 
							 try {
								logger.info("saveWindow" );
								 ResponseEntity<Object>  saveWindowResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/save", HttpMethod.POST, new HttpEntity<Object>(headers),
								            Object.class,windowId);
								 saveWindowResponse.getBody();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Error saveWindow::"+e.getMessage() );
							}
							 
							 
							 
							try { logger.info("selectPagOfWindowFullURI" );
							 ResponseEntity<Object>  performWindowActionResponse = restTemplate.exchange(
									 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{childWindowId}/tools/cb_1", HttpMethod.POST, new HttpEntity<Object>(headers),
						                Object.class,windowId,"1234");
							 performWindowActionResponse.getBody();
							} catch(Exception e) {
								//TODO Auto-generated catch block
								logger.error("Error performWindowAction::"+e.getMessage());
								
							}
							try {
								//Check and terminate if there is any session exists
								logger.info("session end" );
								RequestEntity<Void> requestEntitySessionEnd = new RequestEntity<>(headers, HttpMethod.DELETE, sessionEndFullURI);
								// Make the API call
								ResponseEntity<String> responseSessionEnd = restTemplate.exchange(requestEntitySessionEnd, String.class);
								responseSessionEnd.getBody();
								 
							}catch (Exception e) {
								logger.error("There is no session exists:" +e.getMessage());
							}
					b=true;
					return b;
					 
			
	}

}
