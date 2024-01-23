package com.di.integration.p21.serviceImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.commons.dto.DocumentLinkDTO;
import com.di.commons.helper.DocumentLinkHelper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21DocumentService;

@Service
public class P21DocumentServiceImpl implements P21DocumentService {
	private static final Logger logger = LoggerFactory.getLogger(P21DocumentServiceImpl.class);

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
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Override
	public boolean linkDocument(DocumentLinkDTO documentLinkDTO,@RequestParam(required = false) MasterTenant masterTenantObject) throws Exception {
		boolean b = false;
		
		// Assuming masterTenantRepository is an autowired bean

		MasterTenant masterTenant;

		if (masterTenantObject == null) {
		    String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
		    masterTenant = masterTenantRepository.findByDbName(tenantId);

		    if (masterTenant == null) {
		        // Handle the case where the tenant is not found in the database
		        // You might want to throw an exception or handle it according to your application's requirements
		    }
		} else {
		    masterTenant = masterTenantObject;
		}

		// Now you can use the masterTenant object in your logic


		URI sessionEnd = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/");
		URI sessionEndFullURI = sessionEnd.resolve(sessionEnd.getRawPath());

		URI sessionCreate = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/");
		URI sessionCreatefullURI = sessionCreate.resolve(sessionCreate.getRawPath());

		URI openWindow = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/");
		URI openWindowfullURI = openWindow.resolve(openWindow.getRawPath());

		URI windowMetaData = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/transaction/metadata/RMA");
		URI windowMetaDatafullURI = windowMetaData.resolve(windowMetaData.getRawPath());

		URI windowList = new URI(
				masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/transaction/services?type=Window");
		URI windowListfullURI = windowList.resolve(windowList.getRawPath());

		System.out.println(windowListfullURI.toString());
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);
		try {

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
		logger.info("Session Create Response:" + responseBody);

		logger.info("Open Window URI:" + openWindowfullURI);

		String childWindowId = "";
		for (DocumentLinkHelper documentLinkHelper : documentLinkDTO.getDocumentLinkHelperList()) {
			logger.info("Open Window Starts:" + openWindowfullURI);
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
			logger.info("openWindowResponseBody: " + openWindowResponseBody);

			JSONObject jsonObject = new JSONObject(openWindowResponseBody);
			String windowId = jsonObject.getString("Result");

			logger.info("window id:: " + windowId);

			logger.info("changeDataForAFieldFullURI:");

			CloseableHttpClient httpClient2 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForAFieldUri = String.format(
					masterTenant.getSubdomain()
							+ "/uiserver0/ui/full/v1/window/%s/elements/changedata?datawindowName=%s&fieldName=%s",
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
			CloseableHttpClient httpClient3 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String selectPagOfWindowURI = String.format(
					masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/%s/elements/select?pageName=%s",
					windowId, "DOCUMENT_LINK_DETAIL");
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

				CloseableHttpClient httpClient4 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String runToolOnWindowURI = String.format(
						masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/%s/elements/tools/run", windowId);
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

				JSONObject jsonResponse = new JSONObject(responseBody3);

				// Extract information from the JSON response
				JSONArray events = new JSONArray(jsonResponse.get("Events").toString());
				JSONObject event = events.getJSONObject(0);
				JSONObject eventData = event.getJSONObject("EventData");
				childWindowId = eventData.getString("windowid");
				logger.info("childWindowId Response: " + childWindowId);

				// }

			} catch (Exception e) {
				logger.error("Error Run Tool on Window::" + e.getMessage());
			}
			logger.info("changeDataForALinkNameField URI: ");

			CloseableHttpClient httpClient5 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForALinkNameFieldURI = masterTenant.getSubdomain()
					+ "/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_name}";

			// Use URLEncoder to encode placeholders
			String encodedUri = changeDataForALinkNameFieldURI
					.replace("{windowId}", URLEncoder.encode(childWindowId, StandardCharsets.UTF_8.toString()))
					.replace("{_dw_link}", URLEncoder.encode("_dw_link", StandardCharsets.UTF_8.toString()))
					.replace("{link_name}", URLEncoder.encode("link_name", StandardCharsets.UTF_8.toString()));

			URI changeDataForALinkNameFieldFullURI = new URI(encodedUri);

			HttpPut changeDataForALinkNameFieldRequest = new HttpPut(changeDataForALinkNameFieldFullURI);
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			changeDataForALinkNameFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			String changeDataForALinkNameField = "\"" + documentLinkHelper.getLinkName() + "\"";
			StringEntity entity3 = new StringEntity(changeDataForALinkNameField, ContentType.APPLICATION_JSON);
			changeDataForALinkNameFieldRequest.setEntity(entity3);

			try (CloseableHttpResponse changeDataForALinkNameFieldResponse = httpClient5
					.execute(changeDataForALinkNameFieldRequest)) {
				HttpEntity responseEntity3 = changeDataForALinkNameFieldResponse.getEntity();
				String responseBody4 = EntityUtils.toString(responseEntity3);
				logger.error("changeDataForALinkNameField Response ::" + responseBody4);
			} catch (IOException e) {
				logger.error("Error executing HTTP request: " + e.getMessage(), e);
			}

			logger.error("changeDataForALinkPathField URI ::");

			CloseableHttpClient httpClient6 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String changeDataForALinkPathFieldURI = masterTenant.getSubdomain()
					+ "/uiserver0/ui/full/v1/window/{windowId}/elements/changedata?datawindowName={_dw_link}&fieldName={link_path}";
			changeDataForALinkPathFieldURI = changeDataForALinkPathFieldURI
					.replace("{windowId}", URLEncoder.encode(childWindowId, StandardCharsets.UTF_8))
					.replace("{_dw_link}", URLEncoder.encode("_dw_link", StandardCharsets.UTF_8))
					.replace("{link_path}", URLEncoder.encode("link_path", StandardCharsets.UTF_8));

			HttpPut changeDataForALinkPathFieldRequest = new HttpPut(changeDataForALinkPathFieldURI);
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			changeDataForALinkPathFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			String changeDataForALinkPathField = "\"" + documentLinkHelper.getLinkPath() + "\"";
			StringEntity entity4 = new StringEntity(changeDataForALinkPathField, ContentType.APPLICATION_JSON);
			changeDataForALinkPathFieldRequest.setEntity(entity4);

			try (CloseableHttpResponse changeDataForALinkPathFieldResponse = httpClient6
					.execute(changeDataForALinkPathFieldRequest)) {
				HttpEntity responseEntity4 = changeDataForALinkPathFieldResponse.getEntity();
				String responseBody5 = EntityUtils.toString(responseEntity4);
				logger.error("changeDataForALinkPathField Response ::" + responseBody5);
			} catch (IOException e) {
				logger.error("Error executing HTTP request: " + e.getMessage(), e);
			}

			try {
				logger.info("save child Window");

				CloseableHttpClient httpClient7 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = masterTenant.getSubdomain()
						+ "/uiserver0/ui/full/v1/window/{childWindowId}/save";
				saveWindowURI = saveWindowURI.replace("{childWindowId}",
						URLEncoder.encode(childWindowId, StandardCharsets.UTF_8));

				HttpPost saveWindowRequest = new HttpPost(saveWindowURI);
				saveWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				saveWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				saveWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				String saveWindow = "\"RMA\"";
				StringEntity entity5 = new StringEntity(saveWindow, ContentType.APPLICATION_JSON);
				saveWindowRequest.setEntity(entity5);

				try (CloseableHttpResponse saveWindowResponse = httpClient7.execute(saveWindowRequest)) {
					HttpEntity responseEntity5 = saveWindowResponse.getEntity();
					String responseBody6 = EntityUtils.toString(responseEntity5);
					logger.info("save child Window : " + responseBody6);
				}

			} catch (Exception e) {
				logger.error("Error save child Window::" + e.getMessage(), e);
			}

			CloseableHttpClient httpClient8 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String pressOkURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/{windowId}/tools/cb_ok";
			pressOkURI = pressOkURI.replace("{windowId}", URLEncoder.encode(childWindowId, StandardCharsets.UTF_8));

			HttpPut pressOkRequest = new HttpPut(pressOkURI);
			pressOkRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			pressOkRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			pressOkRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			try (CloseableHttpResponse pressOkResponse = httpClient8.execute(pressOkRequest)) {
				HttpEntity responseEntity5 = pressOkResponse.getEntity();
				String responseBody6 = EntityUtils.toString(responseEntity5);
				logger.info("pressOk " + responseBody6);
			} catch (IOException e) {
				logger.error("Error pressOk::" + e.getMessage(), e);
			}

			try {
				logger.info("save main Window");

				CloseableHttpClient httpClient9 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/{windowId}/save";
				saveWindowURI = saveWindowURI.replace("{windowId}",
						URLEncoder.encode(windowId, StandardCharsets.UTF_8));

				URI saveWindowFullURI = new URIBuilder(saveWindowURI).build();

				HttpPost saveWindowRequest = new HttpPost(saveWindowFullURI);
				saveWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				saveWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				saveWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

				CloseableHttpResponse saveWindowResponse = httpClient9.execute(saveWindowRequest);
				HttpEntity responseEntity6 = saveWindowResponse.getEntity();
				String responseBody7 = EntityUtils.toString(responseEntity6);
				logger.info("save main Window " + responseBody7);

			} catch (Exception e) {
				logger.error("Error save main Window::" + e.getMessage(), e);
			}

			try {
				logger.info("close window");

				CloseableHttpClient httpClient10 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String saveWindowURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/{windowId}";
				saveWindowURI = saveWindowURI.replace("{windowId}",
						URLEncoder.encode(windowId, StandardCharsets.UTF_8));

				HttpDelete request = new HttpDelete(saveWindowURI);
				request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

				try (CloseableHttpResponse response1 = httpClient10.execute(request)) {
					String responseBody3 = EntityUtils.toString(response1.getEntity());
					logger.info("close window " + responseBody3);
				}

			} catch (Exception e) {
				logger.error("Error close window::" + e.getMessage(), e);
			}

		}

		try {
			// Check and terminate if there is any session exists
			logger.info("session end");
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
