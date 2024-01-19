package com.di.integration.p21.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.di.integration.p21.service.P21RmaReceiptService;

@Service
public class RmaReceiptServiceImpl implements P21RmaReceiptService {

	private static final Logger logger = LoggerFactory.getLogger(RmaReceiptServiceImpl.class);

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	ReturnOrderRepository returnOrderRepository;
	
	@Autowired
	AuditLogRepository auditLogRepository;

	private static final String rmaReceiptWindowEndpoint = "/uiserver0/ui/full/v1/window/";
	private static final String changeDataWindow = "/uiserver0/ui/full/v1/window/";
//	MasterTenant masterTenant;
	public static String token;
	
	public Map<String, List<String>> createRmaReceipt(String rmaNo, MasterTenant masterTenant) throws Exception {
		Map<String, List<String>> receiptNoWithItemId = new HashMap<String, List<String>>();
//		String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
//		masterTenant = masterTenantRepository.findByDbName(tenantId);

		// Token for tenant/ERP
		token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("THE TOKEN IS:" + token);
		// Hitting URLS one by one
		// Every API will have content type and accept as application/json.
		// Lets make two global variable to be used anywhere in code ..
		
		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(String.valueOf(rmaNo));
		ReturnOrder returnOrder = findByRmaOrderNo.get();
		List<ReturnOrderItem> returnOrderItems = returnOrder.getReturnOrderItem();
		
		Map<String, String> map = new HashMap<String, String>();
		for (ReturnOrderItem returnOrderItem : returnOrderItems) {
			map.put(returnOrderItem.getItemName(), returnOrderItem.getReturnLocationId());
		}
		
		Map<String, List<ReturnOrderItem>> groupedByLocationId = returnOrderItems.stream().collect(Collectors.groupingBy(ReturnOrderItem::getReturnLocationId));			
		
		for(String locationId : groupedByLocationId.keySet()) {
			String windowId = null;
			String sessionId = null;

//			// First
//			// API---------------------------------------------------------------------------------------------------------------------------------
			String openSession = masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/";
			try {
				logger.info("1) Open Session for RMA Receipt line");

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
			String openWindow = masterTenant.getSubdomain() + rmaReceiptWindowEndpoint;
			try {
	
				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
	
				URIBuilder uriBuilder = new URIBuilder(openWindow);
				HttpPost httpPost = new HttpPost(uriBuilder.build());
	
				httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "application/json");
	
				String requestBodyForWindow = "\"RMAReceipt\"";
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

			// Store session id to use in the end

			// Third API to enter location id
			// Api---------------------------------------
			String changeDataForAField = masterTenant.getSubdomain() + changeDataWindow + windowId
					+ "/elements/changedata?datawindowName=header&fieldName=c_location_id";
			logger.info("Change location id url "+changeDataForAField);
			try {
	
				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
	
				URIBuilder uriBuilder = new URIBuilder(changeDataForAField);
				HttpPut httpPut = new HttpPut(uriBuilder.build());
	
				httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPut.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String selectPagOfWindow = locationId;
				logger.info("Location Id :"+selectPagOfWindow);
				StringEntity entity4 = new StringEntity(selectPagOfWindow, ContentType.APPLICATION_JSON);
				httpPut.setEntity(entity4);
	
	
				CloseableHttpResponse response = httpClient.execute(httpPut);
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity);
				logger.info("Enter location id response :"+jsonResponse);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					logger.info("Third API ran successfully.");
				} else {
					logger.error("Error: Unexpected status code in third api - " + statusCode);
				}
	
			} catch (Exception e) {
				logger.error("Error Changing data for a field :: " + e.getMessage());
			}

			// Store session id to use in the end

			// Fourth API to enter RMA no
			// Api---------------------------------------
			
			String enterRmaNo = masterTenant.getSubdomain() + changeDataWindow + windowId
					+ "/elements/changedata?datawindowName=header&fieldName=order_no";
			logger.info("Entering order no url"+enterRmaNo);
			try {
				

				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				URIBuilder uriBuilder = new URIBuilder(enterRmaNo);
				HttpPut httpPut = new HttpPut(uriBuilder.build());

				httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPut.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String selectPagOfWindow = rmaNo;
				StringEntity entity4 = new StringEntity(selectPagOfWindow, ContentType.APPLICATION_JSON);
				httpPut.setEntity(entity4);

				CloseableHttpResponse response = httpClient.execute(httpPut);
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					String jsonResponse = EntityUtils.toString(entity);
					JSONObject jsonObject = new JSONObject(jsonResponse);
					logger.info(jsonResponse);
					logger.info("Check Prior receipr exist : "+jsonObject.getBoolean("Success"));
					if (!jsonObject.getBoolean("Success")) {
						// Get the "Events" array
						JSONArray eventsArray = jsonObject.getJSONArray("Events");

						// Check if the "Events" array is not empty
						if (eventsArray.length() > 0) {
							// Get the first event object
							JSONObject eventObject = eventsArray.getJSONObject(0);

							// Get the value of "windowid"
							String childWindowId = eventObject.getJSONObject("EventData").getString("windowid");
							logger.info("Child Window Id :"+ childWindowId);
							String getActiveWindow = masterTenant.getSubdomain() + changeDataWindow + childWindowId
									+ "/active";
							CloseableHttpClient childhttpClient = HttpClients.custom()
									.setSSLContext(
											SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
									.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

							URIBuilder childuriBuilder = new URIBuilder(getActiveWindow);
							HttpGet childhttpPut = new HttpGet(childuriBuilder.build());

							childhttpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
							childhttpPut.setHeader("Accept", "application/json");
							childhttpPut.setHeader("Content-Type", "application/json"); // Set the content type to text/plain

							CloseableHttpResponse childresponse = childhttpClient.execute(childhttpPut);
							HttpEntity childentity = childresponse.getEntity();
							String childjsonResponse = EntityUtils.toString(childentity);
							logger.info(childjsonResponse);
							JSONObject childjsonObject = new JSONObject(childjsonResponse);
							
							boolean boolean1 = childjsonObject.getBoolean("Success");
							logger.info("Child window is active: "+boolean1);
							// Now you can use the "windowId" as needed

							if (boolean1) {
								logger.info("Entering No buttton if prior receipt Exist : ");
								enterNoButton(childWindowId, masterTenant);
							}

						}
					}

				} else {
					logger.info("Response not generated from 2nd API.");
				}

			} catch (Exception e) {
				logger.error("Error Creating Window :: " + e.getMessage());
			}

			// Fifth API to confirm
			// Api---------------------------------------
			String enterYes = masterTenant.getSubdomain() + changeDataWindow + windowId
					+ "/elements/changedata?datawindowName=header&fieldName=confirm_receipt";
			logger.info("Chechk on confirm receipt : "+enterYes);
			try {
				
				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				URIBuilder uriBuilder = new URIBuilder(enterYes);
				HttpPut httpPut = new HttpPut(uriBuilder.build());

				httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPut.setHeader("Accept", "application/json");
				httpPut.setHeader("Content-Type", "application/json");

				// Set raw data in the request body
				String requestBodyForRmaNo = "\"Y\"";
				StringEntity stringEntity = new StringEntity(requestBodyForRmaNo);
				httpPut.setEntity(stringEntity);

				CloseableHttpResponse response = httpClient.execute(httpPut);
				HttpEntity childentity = response.getEntity();
				String jsonResponse = EntityUtils.toString(childentity);
				logger.info("Fifth API to confirm"+ jsonResponse);
			} catch (Exception e) {
				logger.error("Error Creating Window :: " + e.getMessage());
			}

			// sixth API to save
			// Api---------------------------------------
			String saveWindow = masterTenant.getSubdomain() + changeDataWindow + windowId + "/save";
			logger.info("Saving the Window : "+saveWindow);
			try {

				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				URIBuilder uriBuilder = new URIBuilder(saveWindow);
				HttpPost httpPost = new HttpPost(uriBuilder.build());

				httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "text/plain"); // Set the content type to text/plain

				CloseableHttpResponse response = httpClient.execute(httpPost);
				HttpEntity childentity = response.getEntity();
				String jsonResponse = EntityUtils.toString(childentity);
				logger.info("SAVE API RESPONSE  :"+jsonResponse);
				JSONObject jsonObject = new JSONObject(jsonResponse);
				if(!jsonObject.getBoolean("Success")) {
					JSONArray eventsArray = jsonObject.getJSONArray("Events");

					// Check if the "Events" array is not empty
					if (eventsArray.length() > 0) {
						// Get the first event object
						JSONObject eventObject = eventsArray.getJSONObject(0);

						// Get the value of "windowid"
						String childWindowId = eventObject.getJSONObject("EventData").getString("windowid");
						String receiptNo = enterYesButton(childWindowId, groupedByLocationId, locationId, masterTenant);
						
						List<ReturnOrderItem> list = groupedByLocationId.get(locationId);
				        List<String> itemNames = list.stream().map(ReturnOrderItem::getItemName).collect(Collectors.toList());
				        receiptNoWithItemId.put(receiptNo, itemNames);
					}
				}else {
					JSONObject json = new JSONObject(jsonResponse);
					logger.info("Receipt No Response : "+json.toString());
			        // Extract the value of __c_receipt_no
					
					String receiptNo = "";
					JSONArray cReceiptNo = json.getJSONArray("Events");
					for (int i = 0; i < cReceiptNo.length(); i++) {
					    JSONObject singleObj = cReceiptNo.getJSONObject(i);
					    if (singleObj.get("Name").equals("savesucceeded")) {
					        JSONObject eventData = singleObj.getJSONObject("EventData");
					        if (eventData.has("__c_receipt_no")) {
					            receiptNo = eventData.getString("__c_receipt_no");
					            break; // Exit the loop once the receipt number is found
					        }
					    }
					}

					List<ReturnOrderItem> list = groupedByLocationId.get(locationId);
					// Create a list of item names
			        List<String> itemNames = list.stream().map(ReturnOrderItem::getItemName).collect(Collectors.toList());
			        receiptNoWithItemId.put(receiptNo, itemNames);
				}

			} catch (Exception e) {
				logger.error("Error Creating Window :: " + e.getMessage());
			}

			// Seventh API to close
			// Api---------------------------------------
			String endSession = masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions";
			try {

				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				URIBuilder uriBuilder = new URIBuilder(endSession);
				HttpDelete httpPost = new HttpDelete(uriBuilder.build());

				httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-Type", "text/plain"); // Set the content type to text/plain

				CloseableHttpResponse response = httpClient.execute(httpPost);

			} catch (Exception e) {
				logger.error("Error Creating Window :: " + e.getMessage());
			}
		}
		return receiptNoWithItemId;

	}

	private String enterYesButton(String childWindowId, Map<String, List<ReturnOrderItem>> groupedByLocationId, String locationId, MasterTenant masterTenant) throws Exception {
		String enterNoButton = masterTenant.getSubdomain() + changeDataWindow + childWindowId + "/tools/cb_1";
		CloseableHttpClient childhttpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URIBuilder childuriBuilder = new URIBuilder(enterNoButton);
		HttpPut childhttpPut = new HttpPut(childuriBuilder.build());

		childhttpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		childhttpPut.setHeader("Accept", "application/json");
		childhttpPut.setHeader("Content-Type", "application/json"); // Set the content type to text/plain

		CloseableHttpResponse childresponse = childhttpClient.execute(childhttpPut);
		HttpEntity childentity = childresponse.getEntity();
		String childjsonResponse = EntityUtils.toString(childentity);
		logger.info(childjsonResponse);
		JSONObject json = new JSONObject(childjsonResponse);

        // Extract the value of __c_receipt_no
		String receiptNo = "";
		JSONArray cReceiptNo = json.getJSONArray("Events");
		for (int i = 0; i < cReceiptNo.length(); i++) {
		    JSONObject singleObj = cReceiptNo.getJSONObject(i);
		    if (singleObj.get("Name").equals("savesucceeded")) {
		        JSONObject eventData = singleObj.getJSONObject("EventData");
		        if (eventData.has("__c_receipt_no")) {
		            receiptNo = eventData.getString("__c_receipt_no");
		            break; // Exit the loop once the receipt number is found
		        }
		    }
		}
		return receiptNo;
		
	}

	void enterNoButton(String childWindowId, MasterTenant masterTenant) throws Exception {

		String enterNoButton = masterTenant.getSubdomain() + changeDataWindow + childWindowId + "/tools/cb_2";
		CloseableHttpClient childhttpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URIBuilder childuriBuilder = new URIBuilder(enterNoButton);
		HttpPut childhttpPut = new HttpPut(childuriBuilder.build());

		childhttpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		childhttpPut.setHeader("Accept", "application/json");
		childhttpPut.setHeader("Content-Type", "application/json"); // Set the content type to text/plain

		CloseableHttpResponse childresponse = childhttpClient.execute(childhttpPut);
		HttpEntity childentity = childresponse.getEntity();
		String childjsonResponse = EntityUtils.toString(childentity);
		logger.info(childjsonResponse);
	}
}
