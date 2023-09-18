package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
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

import com.di.commons.dto.DocumentLinkDTO;
import com.di.commons.helper.DocumentLinkHelper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21DocumentService;
@Service
public class P21DocumentServiceImpl implements P21DocumentService{
	private static final Logger logger = LoggerFactory.getLogger(P21OrderLineServiceImpl.class);

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
	
	@Override
	public boolean linkDocument(DocumentLinkDTO documentLinkDTO) throws Exception {
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
				String childWindowId="";
				 for (DocumentLinkHelper documentLinkHelper : documentLinkDTO.getDocumentLinkHelperList()) {
				ResponseEntity<String> openWindowResponse = restTemplate.postForEntity(openWindowfullURI, httpEntity, String.class);
				openWindowResponse.getBody();
				JSONObject jsonObject = new JSONObject(openWindowResponse.getBody());
				String windowId = jsonObject.getString("Result");
				//ResponseEntity<String> windowMetaDataResponse = restTemplate.getForEntity(windowMetaDatafullURI, hedaers, String.class);
				openWindowResponse.getBody();
				logger.info("window id:: "+windowId);
				
				 
					 logger.info("changeDataForAFieldFullURI:" );
					 
					 ResponseEntity<Object>  changeDataForAFieldResponse = restTemplate.exchange(
							 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={order}&fieldName={order_no}", HttpMethod.PUT, new HttpEntity<Object>("\""+documentLinkDTO.getRmaNo()+"\"",headers),
				                Object.class,windowId,"order","order_no");
					 changeDataForAFieldResponse.getBody();
					 
					 
						 logger.info("selectPagOfWindowFullURI:" );
						 ResponseEntity<Object>  selectPagOfWindowResponse = restTemplate.exchange(
								 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/select?pageName={DOCUMENT_LINK_DETAIL}", HttpMethod.POST, new HttpEntity<Object>(headers),
					                Object.class,windowId,"DOCUMENT_LINK_DETAIL");
						 selectPagOfWindowResponse.getBody();
						 
							
							
						
							 try {
								 logger.info("Run Tool on Window" );
								 ResponseEntity<Object>  runToolOnWindowResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/tools/run?dwName={document_link_detail_detail}&toolName={m_addlink}&dwElementName={document_link_detail_detail}&row={rowNo}", HttpMethod.PUT, new HttpEntity<Object>(headers),
							                Object.class,windowId,"document_link_detail_detail","m_addlink","document_link_detail_detail","1");
								 runToolOnWindowResponse.getBody();
								 
								 runToolOnWindowResponse.getBody();
									
								  Map<String, Object> jsonMap = (Map<String, Object>) runToolOnWindowResponse.getBody();
							        JSONArray events = new JSONArray(jsonMap.get("Events").toString());

							        // Iterate through the events array to find the windowid
							       // for (int i = 0; i < events.length(); i++) {
							            JSONObject event = events.getJSONObject(0);
							            JSONObject eventData = event.getJSONObject("EventData");
							             childWindowId = eventData.getString("windowid");
							       // }
									
								 
							 }catch (Exception e) {
								 logger.error("Error Run Tool on Window::"+e.getMessage());
							}
							
								 
								 ResponseEntity<Object>  changeDataForALinkNameFieldResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_name}", HttpMethod.PUT, new HttpEntity<Object>("\""+documentLinkHelper.getLinkName()+"\"",headers),
							                Object.class,childWindowId,"_dw_link","link_name");
								 changeDataForALinkNameFieldResponse.getBody();
								 
								 ResponseEntity<Object>  changeDataForALinkPathFieldResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_path}", HttpMethod.PUT, new HttpEntity<Object>("\""+documentLinkHelper.getLinkPath()+"\"",headers),
							                Object.class,childWindowId,"_dw_link","link_path");
								 changeDataForALinkPathFieldResponse.getBody();
							
							
							 
							 try {
								logger.info("save child Window" );
								 ResponseEntity<Object>  saveWindowResponse = restTemplate.exchange(
										 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{childWindowId}/save", HttpMethod.POST, new HttpEntity<Object>(headers),
								            Object.class,childWindowId);
								 saveWindowResponse.getBody();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Error save child Window::"+e.getMessage() );
							}
						
							 try {
									logger.info("pressOk" );
									 ResponseEntity<Object>  pressOkResponse = restTemplate.exchange(
											 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{childWindowId}/tools/cb_ok", HttpMethod.PUT, new HttpEntity<Object>(headers),
									            Object.class,childWindowId);
									 pressOkResponse.getBody();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Error pressOk::"+e.getMessage() );
								}
							 
							 try {
									logger.info("save main Window" );
									 ResponseEntity<Object>  saveWindowResponse = restTemplate.exchange(
											 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}/save", HttpMethod.POST, new HttpEntity<Object>(headers),
									            Object.class,windowId);
									 saveWindowResponse.getBody();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Error save main Window::"+e.getMessage() );
								}
							 try {
									logger.info("close window" );
									 ResponseEntity<Object>  saveWindowResponse = restTemplate.exchange(
											 "https://apiplay.labdepotinc.com/uiserver0/ui/full/v1/window/{windowId}", HttpMethod.DELETE, new HttpEntity<Object>(headers),
									            Object.class,windowId);
									 saveWindowResponse.getBody();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Error close window::"+e.getMessage() );
								}
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
	

