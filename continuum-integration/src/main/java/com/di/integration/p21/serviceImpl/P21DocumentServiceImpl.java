package com.di.integration.p21.serviceImpl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
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
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.di.commons.dto.DocumentLinkDTO;
import com.di.commons.helper.DocumentLinkHelper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21DocumentService;

@Service
public class P21DocumentServiceImpl implements P21DocumentService {
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
		boolean b = false;

		URI sessionEnd = new URI("https://65.154.203.155:8443" + "/uiserver0/ui/common/v1/sessions/");
		URI sessionEndFullURI = sessionEnd.resolve(sessionEnd.getRawPath());

		URI sessionCreate = new URI("https://65.154.203.155:8443" + "/uiserver0/ui/common/v1/sessions/");
		URI sessionCreatefullURI = sessionCreate.resolve(sessionCreate.getRawPath());

		URI openWindow = new URI("https://65.154.203.155:8443" + "/uiserver0/ui/full/v1/window/");
		URI openWindowfullURI = openWindow.resolve(openWindow.getRawPath());

		URI windowMetaData = new URI("https://65.154.203.155:8443" + "/uiserver0/ui/full/v1/transaction/metadata/RMA");
		URI windowMetaDatafullURI = windowMetaData.resolve(windowMetaData.getRawPath());

		URI windowList = new URI(
				"https://65.154.203.155:8443" + "/uiserver0/ui/full/v1/transaction/services?type=Window");
		URI windowListfullURI = windowList.resolve(windowList.getRawPath());

		System.out.println(windowListfullURI.toString());
//		HttpHeaders headers = new HttpHeaders();
//		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		// Set the Accept header to receive JSON response
		// headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//		headers.add("Accept","application/json");
//		headers.add("Content-Type", "application/json");
		// headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		// https://65.154.203.155:8443/data/erp/views/v1/p21_view_ord_ack_hdr?$
		// Create the request entity with headers
		String token = p21TokenServiceImpl.getToken();
		logger.info("#### TOKEN #### {}", token);
		try {
			// Check and terminate if there is any session exists
//			RequestEntity<Void> requestEntitySessionEnd = new RequestEntity<>(headers, HttpMethod.DELETE, sessionEndFullURI);
			// Make the API call
//			ResponseEntity<String> responseSessionEnd = restTemplate.exchange(requestEntitySessionEnd, String.class);
//			responseSessionEnd.getBody();

			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			HttpDelete request = new HttpDelete(sessionEndFullURI);
			request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			CloseableHttpResponse response = httpClient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());
			logger.info("Sessione end response " + responseBody);

		} catch (Exception e) {
			logger.error("There is no session exists:" + e.getMessage());
		}

//		 RequestEntity<Void> requestEntitySessionCreate = new RequestEntity<>(headers, HttpMethod.POST, sessionCreatefullURI);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request1 = new HttpPost(sessionCreatefullURI);

		// Set request headers
		request1.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request1.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request1.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		CloseableHttpResponse response = httpClient.execute(request1);
		String responseBody = EntityUtils.toString(response.getEntity());
		logger.info("Session Create URI:" + sessionCreatefullURI);
		logger.info("Session Create Response:" + responseBody);

		// Make the API call
//			ResponseEntity<String> sessionCreateResponse = restTemplate.exchange(requestEntitySessionCreate, String.class);
//			sessionCreateResponse.getBody();

		// RequestEntity<Void> openWindowrequestEntity = new RequestEntity<>(headers,
		// HttpMethod.POST, openWindowfullURI);
		logger.info("Open Window URI:" + openWindowfullURI);
		// Make the API call
		// ResponseEntity<String> openWindowResponse =
		// restTemplate.exchange(openWindowrequestEntity, String.class);
		/*
		 * ResponseEntity<String> openWindowResponse =
		 * restTemplate.exchange(openWindowfullURI, HttpMethod.POST, new
		 * HttpEntity<String>("RMA",headers), String.class);
		 * openWindowResponse.getBody();
		 */

		// HttpHeaders headers1 = new HttpHeaders();
		// headers1.setBearerAuth(p21TokenServiceImpl.getToken());
//				headers.setContentType(MediaType.APPLICATION_JSON);
//				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//				HttpEntity<String> httpEntity = new HttpEntity<>("\"RMA\"", headers);
		String childWindowId = "";
		for (DocumentLinkHelper documentLinkHelper : documentLinkDTO.getDocumentLinkHelperList()) {
			logger.info("Open Window Starts:" + openWindowfullURI);
//				ResponseEntity<String> openWindowResponse = restTemplate.postForEntity(openWindowfullURI, httpEntity, String.class);
//				openWindowResponse.getBody();
			CloseableHttpClient httpClient1 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			HttpPost openWindowRequest = new HttpPost(openWindowfullURI);

			// Set request headers
			openWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			openWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			openWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			StringEntity entity = new StringEntity("\"RMA\"", ContentType.APPLICATION_JSON);
			openWindowRequest.setEntity(entity);
			CloseableHttpResponse openWindowResponse = httpClient1.execute(openWindowRequest);
			String openWindowResponseBody = EntityUtils.toString(openWindowResponse.getEntity());

			JSONObject jsonObject = new JSONObject(openWindowResponseBody);
			String windowId = jsonObject.getString("Result");

			logger.info("window id:: " + windowId);

			logger.info("changeDataForAFieldFullURI:");

//			ResponseEntity<Object> changeDataForAFieldResponse = restTemplate.exchange(
//					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={order}&fieldName={order_no}",
//					HttpMethod.PUT, new HttpEntity<Object>("\"" + documentLinkDTO.getRmaNo() + "\"", headers),
//					Object.class, windowId, "order", "order_no");
//			changeDataForAFieldResponse.getBody();
			CloseableHttpClient httpClient2 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForAFieldUri = String.format(
					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/%s/elements/changedata?datawindowName=%s&fieldName=%s",
					windowId, "order", "order_no");
			URI changeDataForAFieldFullURI = new URIBuilder(changeDataForAFieldUri).build();

			HttpPut changeDataForAFieldRequest = new HttpPut(changeDataForAFieldFullURI);
			changeDataForAFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			changeDataForAFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			changeDataForAFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			String requestBody = "\"" + documentLinkDTO.getRmaNo() + "\"";
			StringEntity entity1 = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
			changeDataForAFieldRequest.setEntity(entity1);
			CloseableHttpResponse changeDataForAFieldResponse = httpClient2.execute(changeDataForAFieldRequest);
			HttpEntity responseEntity = changeDataForAFieldResponse.getEntity();
			String responseBody1 = EntityUtils.toString(responseEntity);
			logger.info("changeDataForAFieldFullURI Response :" + responseBody1);

			logger.info("selectPagOfWindowFullURI:");
//			ResponseEntity<Object> selectPagOfWindowResponse = restTemplate.exchange(
//					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/select?pageName={DOCUMENT_LINK_DETAIL}",
//					HttpMethod.POST, new HttpEntity<Object>(headers), Object.class, windowId, "DOCUMENT_LINK_DETAIL");
//			selectPagOfWindowResponse.getBody();
			CloseableHttpClient httpClient3 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String selectPagOfWindowURI = String.format(
					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/%s/elements/select?pageName=%s", windowId,
					"DOCUMENT_LINK_DETAIL");
			URI selectPagOfWindowFullURI = new URIBuilder(selectPagOfWindowURI).build();

			HttpPost selectPagOfWindowRequest = new HttpPost(selectPagOfWindowFullURI);
			selectPagOfWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			selectPagOfWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			selectPagOfWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			String selectPagOfWindow = "\"" + documentLinkDTO.getRmaNo() + "\"";
			StringEntity entity2 = new StringEntity(selectPagOfWindow, ContentType.APPLICATION_JSON);
			selectPagOfWindowRequest.setEntity(entity2);
			CloseableHttpResponse selectPagOfWindowResponse = httpClient3.execute(selectPagOfWindowRequest);
			HttpEntity responseEntity1 = selectPagOfWindowResponse.getEntity();
			String responseBody2 = EntityUtils.toString(responseEntity1);
			logger.info("selectPagOfWindowFullURI Response :" + responseBody2);

			try {
				logger.info("Run Tool on Window");
//				ResponseEntity<Object> runToolOnWindowResponse = restTemplate.exchange(
//						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/tools/run?dwName={document_link_detail_detail}&toolName={m_addlink}&dwElementName={document_link_detail_detail}&row={rowNo}",
//						HttpMethod.PUT, new HttpEntity<Object>(headers), Object.class, windowId,
//						"document_link_detail_detail", "m_addlink", "document_link_detail_detail", "1");
//				runToolOnWindowResponse.getBody();
//
//				runToolOnWindowResponse.getBody();

				CloseableHttpClient httpClient4 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String runToolOnWindowURI = String.format(
						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/%s/elements/tools/run", windowId);
				URIBuilder uriBuilder = new URIBuilder(runToolOnWindowURI)
						.addParameter("dwName", "document_link_detail_detail").addParameter("toolName", "m_addlink")
						.addParameter("dwElementName", "document_link_detail_detail").addParameter("row", "1");

				URI runToolOnWindowFullURI = uriBuilder.build();

				HttpPut runToolOnWindowRequest = new HttpPut(runToolOnWindowFullURI);
				runToolOnWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				runToolOnWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				runToolOnWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String runToolOnWindow = "\"RMA\"";
				StringEntity entity3 = new StringEntity(runToolOnWindow, ContentType.APPLICATION_JSON);
				runToolOnWindowRequest.setEntity(entity3);

				CloseableHttpResponse runToolOnWindowResponse = httpClient4.execute(runToolOnWindowRequest);
				HttpEntity responseEntity2 = runToolOnWindowResponse.getEntity();
				String responseBody3 = EntityUtils.toString(responseEntity2);
				logger.info("Run Tool on Window Response: " + responseBody3);


//				Map<String, Object> jsonMap = (Map<String, Object>) responseEntity2;
//				JSONArray events = new JSONArray(jsonMap.get("Events").toString());
//
//				// Iterate through the events array to find the windowid
//				// for (int i = 0; i < events.length(); i++) {
//				JSONObject event = events.getJSONObject(0);
//				JSONObject eventData = event.getJSONObject("EventData");
//				childWindowId = eventData.getString("windowid");
				JSONObject jsonResponse = new JSONObject(responseBody3);

				// Extract information from the JSON response
				JSONArray events = new JSONArray(jsonResponse.get("Events").toString());
				JSONObject event = events.getJSONObject(0);
				JSONObject eventData = event.getJSONObject("EventData");
				childWindowId = eventData.getString("windowid");
				// }

			} catch (Exception e) {
				logger.error("Error Run Tool on Window::" + e.getMessage());
			}
			logger.info("changeDataForALinkNameField URI: ");

//			ResponseEntity<Object> changeDataForALinkNameFieldResponse = restTemplate.exchange(
//					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_name}",
//					HttpMethod.PUT, new HttpEntity<Object>("\"" + documentLinkHelper.getLinkName() + "\"", headers),
//					Object.class, childWindowId, "_dw_link", "link_name");
//			changeDataForALinkNameFieldResponse.getBody();

			CloseableHttpClient httpClient5 = HttpClients.custom()
			        .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
			        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForALinkNameFieldURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_name}";
			URI changeDataForALinkNameFieldFullURI = new URIBuilder(changeDataForALinkNameFieldURI)
			        .addParameter("windowId", windowId)
			        .addParameter("_dw_link", "_dw_link")
			        .addParameter("link_name", "link_name")
			        .build();

			HttpPut changeDataForALinkNameFieldRequest = new HttpPut(changeDataForALinkNameFieldFullURI);
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			String changeDataForALinkNameField = "\"" + documentLinkHelper.getLinkName() + "\"";
			StringEntity entity3 = new StringEntity(changeDataForALinkNameField, ContentType.APPLICATION_JSON);
			changeDataForALinkNameFieldRequest.setEntity(entity3);

			try (CloseableHttpResponse changeDataForALinkNameFieldResponse = httpClient5.execute(changeDataForALinkNameFieldRequest)) {
			    HttpEntity responseEntity3 = changeDataForALinkNameFieldResponse.getEntity();
			    String responseBody4 = EntityUtils.toString(responseEntity3);
			    logger.error("changeDataForALinkNameField Response ::" + responseBody4);
			} catch (IOException e) {
			    logger.error("Error executing HTTP request: " + e.getMessage(), e);
			}

//			ResponseEntity<Object> changeDataForALinkPathFieldResponse = restTemplate.exchange(
//					"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_path}",
//					HttpMethod.PUT, new HttpEntity<Object>("\"" + documentLinkHelper.getLinkPath() + "\"", headers),
//					Object.class, childWindowId, "_dw_link", "link_path");
//			changeDataForALinkPathFieldResponse.getBody();
			logger.error("changeDataForALinkPathField URI ::");

			CloseableHttpClient httpClient6 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForALinkPathFieldURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_path}";
			URI changeDataForALinkPathFieldFullURI = new URIBuilder(changeDataForALinkPathFieldURI)
					.addParameter("windowId", childWindowId).addParameter("_dw_link", "_dw_link")
					.addParameter("link_path", "link_path").build();

			HttpPut changeDataForALinkPathFieldRequest = new HttpPut(changeDataForALinkPathFieldFullURI);
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			String changeDataForALinkPathField = "\"" + documentLinkHelper.getLinkPath() + "\"";
			StringEntity entity4 = new StringEntity(changeDataForALinkPathField, ContentType.APPLICATION_JSON);
			changeDataForALinkPathFieldRequest.setEntity(entity4);
			CloseableHttpResponse changeDataForALinkPathFieldResponse = httpClient6
					.execute(changeDataForALinkPathFieldRequest);
			HttpEntity responseEntity4 = changeDataForALinkPathFieldResponse.getEntity();
			String responseBody5 = EntityUtils.toString(responseEntity4);
			logger.error("changeDataForALinkPathField Response ::" + responseBody5);

			try {
				logger.info("save child Window");
//				ResponseEntity<Object> saveWindowResponse = restTemplate.exchange(
//						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{childWindowId}/save", HttpMethod.POST,
//						new HttpEntity<Object>(headers), Object.class, childWindowId);
//				saveWindowResponse.getBody();

				CloseableHttpClient httpClient7 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{childWindowId}/save";
				URI saveWindowFullURI = new URIBuilder(saveWindowURI).addParameter("childWindowId", childWindowId)
						.build();

				HttpPost saveWindowRequest = new HttpPost(saveWindowFullURI);
				saveWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				saveWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				saveWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String saveWindow = "\"RMA\"";
				StringEntity entity5 = new StringEntity(saveWindow, ContentType.APPLICATION_JSON);
				saveWindowRequest.setEntity(entity5);
				CloseableHttpResponse saveWindowResponse = httpClient7.execute(saveWindowRequest);
				HttpEntity responseEntity5 = saveWindowResponse.getEntity();
				String responseBody6 = EntityUtils.toString(responseEntity5);
				logger.info("save child Window : " + responseBody6);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error save child Window::" + e.getMessage());
			}

			try {
				logger.info("pressOk");
//				ResponseEntity<Object> pressOkResponse = restTemplate.exchange(
//						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{childWindowId}/tools/cb_ok",
//						HttpMethod.PUT, new HttpEntity<Object>(headers), Object.class, childWindowId);
//				pressOkResponse.getBody();
				CloseableHttpClient httpClient8 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String pressOkURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_path}";
				URI pressOkFullURI = new URIBuilder(pressOkURI).addParameter("windowId", childWindowId)
						.addParameter("_dw_link", "_dw_link").addParameter("link_path", "link_path").build();

				HttpPut pressOkRequest = new HttpPut(pressOkFullURI);
				pressOkRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				pressOkRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				pressOkRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String pressOk = "\"" + documentLinkHelper.getLinkPath() + "\"";
				StringEntity entity5 = new StringEntity(pressOk, ContentType.APPLICATION_JSON);
				pressOkRequest.setEntity(entity5);
				CloseableHttpResponse pressOkResponse = httpClient8.execute(pressOkRequest);
				HttpEntity responseEntity5 = pressOkResponse.getEntity();
				String responseBody6 = EntityUtils.toString(responseEntity5);
				logger.info("pressOk " + responseBody6);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error pressOk::" + e.getMessage());
			}

			try {
				logger.info("save main Window");
//				ResponseEntity<Object> saveWindowResponse = restTemplate.exchange(
//						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/save", HttpMethod.POST,
//						new HttpEntity<Object>(headers), Object.class, windowId);
//				saveWindowResponse.getBody();

				CloseableHttpClient httpClient9 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}/save";
				URI saveWindowFullURI = new URIBuilder(saveWindowURI).addParameter("windowId", windowId).build();

				HttpPost saveWindowRequest = new HttpPost(saveWindowFullURI);
				saveWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				saveWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				saveWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String saveWindow = "\"RMA\"";
				StringEntity entity6 = new StringEntity(saveWindow, ContentType.APPLICATION_JSON);
				saveWindowRequest.setEntity(entity6);
				CloseableHttpResponse saveWindowResponse = httpClient9.execute(saveWindowRequest);
				HttpEntity responseEntity5 = saveWindowResponse.getEntity();
				String responseBody6 = EntityUtils.toString(responseEntity5);
				logger.info("save main Window " + responseBody6);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error save main Window::" + e.getMessage());
			}
			try {
				logger.info("close window");
//				ResponseEntity<Object> saveWindowResponse = restTemplate.exchange(
//						"https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}", HttpMethod.DELETE,
//						new HttpEntity<Object>(headers), Object.class, windowId);
//				saveWindowResponse.getBody();

				CloseableHttpClient httpClient10 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = "https://65.154.203.155:8443/uiserver0/ui/full/v1/window/{windowId}";
				URI saveWindowFullURI = new URIBuilder(saveWindowURI).addParameter("windowId", windowId).build();

				HttpDelete request = new HttpDelete(saveWindowFullURI);
				request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

				request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse response1 = httpClient10.execute(request);
				String responseBody3 = EntityUtils.toString(response1.getEntity());
				logger.info("close window " + responseBody3);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error close window::" + e.getMessage());
			}
		}

		try {
			// Check and terminate if there is any session exists
			logger.info("session end");
//			RequestEntity<Void> requestEntitySessionEnd = new RequestEntity<>(headers, HttpMethod.DELETE,
//					sessionEndFullURI);
//			// Make the API call
//			ResponseEntity<String> responseSessionEnd = restTemplate.exchange(requestEntitySessionEnd, String.class);
//			responseSessionEnd.getBody();

			CloseableHttpClient httpClient9 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			HttpDelete request = new HttpDelete(sessionEndFullURI);
			request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			CloseableHttpResponse response1 = httpClient9.execute(request);
			String responseBody1 = EntityUtils.toString(response1.getEntity());
			logger.info("session end " + responseBody1);

		} catch (Exception e) {
			logger.error("There is no session exists:" + e.getMessage());
		}
		b = true;
		return b;

	}
}
