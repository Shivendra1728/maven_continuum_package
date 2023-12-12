package com.di.integration.p21.serviceImpl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21InvoiceMapper;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21InvoiceService;

@Service
public class P21InvoiceServiceImpl implements P21InvoiceService {
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

	@Autowired
	P21OrderLineServiceImpl p21OrderLineServiceImpl;

	@Autowired
	P21OrderLineItemMapper p21orderLineItemMapper;

	@Autowired
	P21InvoiceMapper p21InvoiceMapper;

	@Autowired
	P21OrderMapper p21OrderMapper;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	StoreDTO storeDTO;

	LocalDate localDate;

	public String getInvoiceLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareInvoiceLineURI(orderSearchParameters, totalItem);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.getToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		logger.info("This has been responded: " + response);

		return EntityUtils.toString(entity);

	}

	private URI prepareInvoiceLineURI(OrderSearchParameters orderSearchParameters, int totalItem)
			throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			System.out.println("Invoice no" + orderSearchParameters.getInvoiceNo());
			filter.append(IntegrationConstants.INVOICE_NO).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(orderSearchParameters.getInvoiceNo()).append("'");
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

			filter.append(IntegrationConstants.BILL_TO_POSTAL_CODE).append(" ")
					.append(IntegrationConstants.CONDITION_EQ).append(" '").append(orderSearchParameters.getZipcode())
					.append("'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append(IntegrationConstants.CUSTOMER_ID).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(orderSearchParameters.getCustomerId()).append("'");

		}

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter=" + encodedFilter;
			if (totalItem == 1) {
				query = query + "&$top=1";
			}
			URI uri = new URI(masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_API_INVOICE_VIEW);
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
		boolean b = false;

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

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
		String token = p21TokenServiceImpl.getToken(masterTenant);
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
			logger.info(responseBody);
		} catch (Exception e) {
			logger.error("There is no session exists:" + e.getMessage());
		}

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

		logger.info("Open Window URI:" + openWindowfullURI);

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
		logger.info("Open Window Response :" + openWindowResponseBody);

		JSONObject jsonObject = new JSONObject(openWindowResponseBody);
		String windowId = jsonObject.getString("Result");

		logger.info("windowMetaDataResponse URI:" + windowMetaDatafullURI);
//		ResponseEntity<Object> windowMetaDataResponse = restTemplate.exchange(windowMetaDatafullURI, HttpMethod.GET,
//				new HttpEntity<Object>(headers), Object.class);

		CloseableHttpClient httpClient2 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		HttpGet windowMetaDataRequest = new HttpGet(windowMetaDatafullURI);

		// Set request headers
		windowMetaDataRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		windowMetaDataRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		windowMetaDataRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		// Execute the request
		HttpResponse windowMetaDataResponse = httpClient2.execute(windowMetaDataRequest);
		HttpEntity entity1 = windowMetaDataResponse.getEntity();
		String windowMetaDataResponseBody = EntityUtils.toString(entity1);
		logger.info("windowMetaData Response :" + windowMetaDataResponseBody);

		logger.info("windowListfullURI:" + windowListfullURI);

		CloseableHttpClient httpClient3 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String windowListURI = "" + masterTenant.getSubdomain()
				+ "/uiserver0/ui/full/v1/transaction/services?type=Window";
//		URI windowListfullURi = new URIBuilder(windowListURI).addParameter("Window", "Window").build();
		URIBuilder uriBuilder = new URIBuilder(windowListURI);

		HttpGet windowListRequest = new HttpGet(uriBuilder.build());

		// Set request headers
		windowListRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		windowListRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		windowListRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse windowListResponse = httpClient3.execute(windowListRequest);
		HttpEntity entity2 = windowListResponse.getEntity();
		String windowListResponseBody = EntityUtils.toString(entity2);
		logger.info("windowListfullURI Response:" + windowListResponseBody);

		/*
		 * restTemplate.exchange(
		 * "http://my-rest-url.org/rest/account/{account}?name={name}", HttpMethod.GET,
		 * httpEntity, Object.class, "my-account", "my-name" );
		 */

		// URI changeDataForAField = new URI("https://65.154.203.155:8443" +
		// "/uiserver0/ui/full/v1/window/"+windowId+"/elements/changedata?datawindowName=order&fieldName=order_no");
		// URI changeDataForAFieldFullURI =
		// changeDataForAField.resolve(changeDataForAField.getRawPath());
		logger.info("changeDataForAFieldFullURI:");

		CloseableHttpClient httpClient4 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String changeDataForAFieldUri = String.format(
				"" + masterTenant.getSubdomain()
						+ "/uiserver0/ui/full/v1/window/%s/elements/changedata?datawindowName=%s&fieldName=%s",
				windowId, "order", "order_no");

		URI changeDataForAFieldFullURI = new URIBuilder(changeDataForAFieldUri).build();

		HttpPut changeDataForAFieldRequest = new HttpPut(changeDataForAFieldFullURI);
		changeDataForAFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		changeDataForAFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		changeDataForAFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		// Use the correct request body
		String requestBody = "\"" + rmaNo + "\"";
		StringEntity entity3 = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
		changeDataForAFieldRequest.setEntity(entity3);

		CloseableHttpResponse changeDataForAFieldResponse = httpClient4.execute(changeDataForAFieldRequest);
		HttpEntity responseEntity = changeDataForAFieldResponse.getEntity();
		String changeDataForAFieldResponseBody = EntityUtils.toString(responseEntity);
		logger.info("changeDataForAFieldFullURI Response :" + changeDataForAFieldResponseBody);

		logger.info("selectPagOfWindowFullURI:");

		CloseableHttpClient httpClient5 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String selectPagOfWindowURI = "" + masterTenant.getSubdomain()
				+ "/uiserver0/ui/full/v1/window/%s/elements/select?pageName=%s";
		URI selectPagOfWindowFullURI = new URIBuilder(String.format(selectPagOfWindowURI,
				URLEncoder.encode(windowId, StandardCharsets.UTF_8.toString()), "tabpage_saleshistory")).build();

		HttpPost selectPagOfWindowRequest = new HttpPost(selectPagOfWindowFullURI);
		selectPagOfWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		selectPagOfWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		selectPagOfWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		String selectPagOfWindow = "\"" + 423651 + "\"";
		StringEntity entity4 = new StringEntity(selectPagOfWindow, ContentType.APPLICATION_JSON);
		selectPagOfWindowRequest.setEntity(entity4);

		try (CloseableHttpResponse selectPagOfWindowResponse = httpClient5.execute(selectPagOfWindowRequest)) {
			HttpEntity responseEntity1 = selectPagOfWindowResponse.getEntity();
			String responseBody2 = EntityUtils.toString(responseEntity1);
			logger.info("selectPagOfWindowFullURI Response :" + responseBody2);
		} catch (IOException e) {
			e.printStackTrace(); // Handle exception appropriately
		}

		URI activeWindowDefinition = new URI(
				masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId + "/active");
		URI activeWindowDefinitionfullURI = activeWindowDefinition.resolve(activeWindowDefinition.getRawPath());

		URI getActiveWindows = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/window/all");
		URI getActiveWindowsfullURI = getActiveWindows.resolve(getActiveWindows.getRawPath());

		logger.info("getToolsOfWindowTab:");

		CloseableHttpClient httpClient6 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String getToolsOfWindowTabURI = "" + masterTenant.getSubdomain()
				+ "/uiserver0/ui/full/v1/window/%s/elements/tools?datawindowName=%s";
		URI getToolsOfWindowTabFullURI = new URIBuilder(String.format(getToolsOfWindowTabURI,
				URLEncoder.encode(windowId, StandardCharsets.UTF_8.toString()), "tabpage_saleshistory")).build();
		HttpGet getToolsOfWindowTabRequest = new HttpGet(getToolsOfWindowTabFullURI);

		// Set request headers
		getToolsOfWindowTabRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		getToolsOfWindowTabRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		getToolsOfWindowTabRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse getToolsOfWindowTabResponse = httpClient6.execute(getToolsOfWindowTabRequest);
		HttpEntity entity5 = getToolsOfWindowTabResponse.getEntity();
		String getToolsOfWindowTabResponseBody = EntityUtils.toString(entity5);

		logger.info("getToolsOfWindowTab Response :" + getToolsOfWindowTabResponseBody);

		CloseableHttpClient httpClient7 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String toolsOfWindowFieldURI = String.format(
				"" + masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/%s/elements/tools?datawindowName=%s",
				windowId, "tabpage_saleshistory");
		URI toolsOfWindowFieldFullURI = new URIBuilder(toolsOfWindowFieldURI).build();

		HttpGet toolsOfWindowFieldRequest = new HttpGet(toolsOfWindowFieldFullURI);

		// Set request headers
		toolsOfWindowFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		toolsOfWindowFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		toolsOfWindowFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse toolsOfWindowFieldResponse = httpClient7.execute(toolsOfWindowFieldRequest);
		HttpEntity entity6 = toolsOfWindowFieldResponse.getEntity();
		String toolsOfWindowFieldResponseBody = EntityUtils.toString(entity6);

		logger.info("toolsOfWindowFieldResponseBody Response " + toolsOfWindowFieldResponseBody);

		CloseableHttpClient httpClient8 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		String currentRowForDataWindowURI = String.format(
				"" + masterTenant.getSubdomain()
						+ "/uiserver0/ui/full/v1/window/%s/elements/activerow?datawindowName=%s",
				windowId, "tabpage_saleshistory");
		URI currentRowForDataWindowFullURI = new URIBuilder(currentRowForDataWindowURI).build();

		HttpGet currentRowForDataWindowRequest = new HttpGet(currentRowForDataWindowFullURI);

		// Set request headers
		currentRowForDataWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		currentRowForDataWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		currentRowForDataWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse currentRowForDataWindowResponse = httpClient8.execute(currentRowForDataWindowRequest);
		HttpEntity entity7 = currentRowForDataWindowResponse.getEntity();
		String currentRowForDataWindowResponseBody = EntityUtils.toString(entity7);

		logger.info("currentRowForDataWindowResponseBody Response " + currentRowForDataWindowResponseBody);

		CloseableHttpClient httpClient9 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		HttpGet activeWindowDefinitionRequest = new HttpGet(activeWindowDefinitionfullURI);

		// Set request headers
		activeWindowDefinitionRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		activeWindowDefinitionRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		activeWindowDefinitionRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse activeWindowDefinitionResponse = httpClient9.execute(activeWindowDefinitionRequest);
		HttpEntity entity8 = activeWindowDefinitionResponse.getEntity();
		String activeWindowDefinitionResponseBody = EntityUtils.toString(entity8);

		logger.info("activeWindowDefinitionResponseBody " + activeWindowDefinitionResponseBody);

		CloseableHttpClient httpClient10 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		HttpGet getActiveWindowsRequest = new HttpGet(getActiveWindowsfullURI);

		// Set request headers
		getActiveWindowsRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		getActiveWindowsRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		getActiveWindowsRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		// Execute the request
		HttpResponse getActiveWindowsResponse = httpClient10.execute(getActiveWindowsRequest);
		HttpEntity entity9 = getActiveWindowsResponse.getEntity();
		String getActiveWindowsResponseBody = EntityUtils.toString(entity9);
		logger.info("getActiveWindowsResponseBody " + getActiveWindowsResponseBody);

		try {
			logger.info("Set Focus on specified Field:");

			CloseableHttpClient httpClient11 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String setFocusOnSpecifiedFieldURI = String.format(
					"" + masterTenant.getSubdomain()
							+ "/uiserver0/ui/full/v1/window/%s/elements/focus?datawindowName=%s&fieldName=%s&row=%s",
					windowId, "tabpage_saleshistory", "invoice_no", "3");
			URI setFocusOnSpecifiedFieldFullURI = new URIBuilder(setFocusOnSpecifiedFieldURI).build();
			HttpPost setFocusOnSpecifiedFieldRequest = new HttpPost(setFocusOnSpecifiedFieldFullURI);
			setFocusOnSpecifiedFieldRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			setFocusOnSpecifiedFieldRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			setFocusOnSpecifiedFieldRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			CloseableHttpResponse setFocusOnSpecifiedFieldResponse = httpClient11
					.execute(setFocusOnSpecifiedFieldRequest);
			HttpEntity entity10 = setFocusOnSpecifiedFieldResponse.getEntity();
			String setFocusOnSpecifiedFieldResponseBody = EntityUtils.toString(entity10);

			logger.info("Set Focus on specified Field Response :" + setFocusOnSpecifiedFieldResponseBody);

		} catch (Exception e) {
			logger.error("Error Set Focus on specified field:: " + e.getMessage());
		}

		try {
			logger.info("Run Tool on Window");

			CloseableHttpClient httpClient12 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String runToolOnWindowURI = String.format("" + masterTenant.getSubdomain()
					+ "/uiserver0/ui/full/v1/window/%s/elements/tools/run?dwName=%s&toolName=%s&dwElementName=%s&row=%s",
					windowId, "tabpage_saleshistory", "m_linktothisrmaline", "tabpage_saleshistory", "3");
			URI runToolOnWindowFullURI = new URIBuilder(runToolOnWindowURI).build();

			HttpPut runToolOnWindowRequest = new HttpPut(runToolOnWindowFullURI);
			runToolOnWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			runToolOnWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			runToolOnWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			CloseableHttpResponse runToolOnWindowResponse = httpClient12.execute(runToolOnWindowRequest);
			HttpEntity entity11 = runToolOnWindowResponse.getEntity();
			String runToolOnWindowResponseBody = EntityUtils.toString(entity11);
			logger.info("Run Tool on Window " + runToolOnWindowResponseBody);

		} catch (Exception e) {
			logger.error("Error Run Tool on Window::" + e.getMessage());
		}

		try {
			logger.info("saveWindow");
			CloseableHttpClient httpClient13 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String saveWindowURI = String
					.format("" + masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/%s/save", windowId);
			URI saveWindowFullURI = new URIBuilder(saveWindowURI).build();

			HttpPost saveWindowRequest = new HttpPost(saveWindowFullURI);
			saveWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			saveWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			saveWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			CloseableHttpResponse saveWindowResponse = httpClient13.execute(saveWindowRequest);
			HttpEntity responseEntity5 = saveWindowResponse.getEntity();
			String responseBody6 = EntityUtils.toString(responseEntity5);
			logger.info("saveWindow " + responseBody6);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error saveWindow::" + e.getMessage());
		}

		try {
			logger.info("selectPagOfWindowFullURI");

			CloseableHttpClient httpClient13 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String performWindowActionURI = String
					.format("" + masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/%s/tools/cb_1", windowId);
			URI performWindowActionFullURI = new URIBuilder(performWindowActionURI).build();

			HttpPost performWindowActionRequest = new HttpPost(performWindowActionFullURI);
			performWindowActionRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			performWindowActionRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			performWindowActionRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			CloseableHttpResponse performWindowActionResponse = httpClient13.execute(performWindowActionRequest);
			HttpEntity responseEntity5 = performWindowActionResponse.getEntity();
			String responseBody6 = EntityUtils.toString(responseEntity5);
			logger.info("selectPagOfWindowFullURI " + responseBody6);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error performWindowAction::" + e.getMessage());

		}
		try {
			// Check and terminate if there is any session exists
			logger.info("session end");

			CloseableHttpClient httpClient14 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			HttpDelete request = new HttpDelete(sessionEndFullURI);
			request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			CloseableHttpResponse response1 = httpClient14.execute(request);
			String responseBody3 = EntityUtils.toString(response1.getEntity());
			logger.info("session end " + responseBody3);

		} catch (Exception e) {
			logger.error("There is no session exists:" + e.getMessage());
		}
		b = true;
		return b;

	}

}
