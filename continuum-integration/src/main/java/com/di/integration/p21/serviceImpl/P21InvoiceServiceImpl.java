package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.SerialData;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21InvoiceMapper;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21InvoiceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21InvoiceServiceImpl implements P21InvoiceService {
	private static final Logger logger = LoggerFactory.getLogger(P21InvoiceServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_INVOICE_VIEW)
	String DATA_API_INVOICE_VIEW;

	@Value(IntegrationConstants.ERP_DATA_API_INVOICE_LINE_VIEW)
	String DATA_API_INVOICE_LINE_VIEW;

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
	TemplateRenderrer templateRenderrer;

	@Autowired
	SendMail emailSender;

	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	private String rmaGetEndPoint;

	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Autowired
	StoreDTO storeDTO;

	LocalDate localDate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String getInvoiceLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareInvoiceLineURI(orderSearchParameters, totalItem);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

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

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");

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
	public boolean linkInvoice(String rmaNo, MasterTenant masterTenantObject) throws Exception {
		boolean b = false;

		MasterTenant masterTenant;
		String windowId = "";
		String childWindowId = "";
		int totalVerificationRows = 0;
		boolean isEnabled = false;
		String invoiceLinkingError = "";

		if (masterTenantObject == null) {
//			String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
			String tenantId = httpServletRequest.getHeader("tenant");
			masterTenant = masterTenantRepository.findByDbName(tenantId);
			masterTenantObject = masterTenant;
		} else {
			masterTenant = masterTenantObject;
		}

		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(rmaNo);
		ReturnOrder returnOrder = findByRmaOrderNo.get();
		String orderNo = returnOrder.getOrderNo();
		List<ReturnOrderItem> items = returnOrder.getReturnOrderItem();

		logger.info("These are items in the RMA: " + items);
		logger.info(orderNo);

		for (int i = 0; i < items.size(); i++) {

			ReturnOrderItem item = items.get(i);
			Integer invoiceToLink = item.getInvoiceNo();
			if (invoiceToLink == null) {
				break;
			}
			Integer indexOfInvNo = 0;
			Integer totalRows = 0;
			Integer quantity = item.getQuanity();
			String itemName = item.getItemName();

			URI sessionEnd = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/");
			URI sessionEndFullURI = sessionEnd.resolve(sessionEnd.getRawPath());

			URI sessionCreate = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/common/v1/sessions/");
			URI sessionCreatefullURI = sessionCreate.resolve(sessionCreate.getRawPath());

			URI openWindow = new URI(masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/");
			URI openWindowfullURI = openWindow.resolve(openWindow.getRawPath());

			String token = p21TokenServiceImpl.findToken(masterTenant);
			logger.info("#### TOKEN #### {}", token);

			// ---------------- End session if exists ----------------------------------
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

			// ----------------- Create session ------------------------------
			logger.info("Session Create URI:" + sessionCreatefullURI);
			try {
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
			} catch (Exception e) {
				logger.info("Error Creating session : " + e.getMessage());
			}

			// -------------Open RMA window ----------------------------
			logger.info("Open Window URI:" + openWindowfullURI);
			try {
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
				windowId = jsonObject.getString("Result");
				logger.info("Window id : " + windowId);
			} catch (Exception e) {
				logger.info("Error Opening RMA Window : " + e.getMessage());
			}

			// ------------------ Change data for a field ------------------------
			// This will open the window for given RMA
			String changeDataForAField = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId
					+ "/elements/changedata?datawindowName=order&fieldName=order_no";
			logger.info("Change data for field URI : " + changeDataForAField);

			try {
				CloseableHttpClient httpClient = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				URIBuilder uriBuilder = new URIBuilder(changeDataForAField);
				HttpPut httpPut = new HttpPut(uriBuilder.build());
				httpPut.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				httpPut.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				httpPut.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				logger.info("RMA No :" + rmaNo);
				StringEntity entity4 = new StringEntity(rmaNo, ContentType.APPLICATION_JSON);
				httpPut.setEntity(entity4);

				CloseableHttpResponse response = httpClient.execute(httpPut);
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity);
				logger.info("Change data for field response :" + jsonResponse);
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					logger.info("Third API ran successfully.");
				} else {
					logger.error("Error: Unexpected status code in third api - " + statusCode);
				}
			} catch (Exception e) {
				logger.error("Error Changing data for a field :: " + e.getMessage());
			}

			// ---------------------- Set focus on specified item ---------------------
			// This API will focus on the item in the item tab which we want to link
			Integer itemIndex = i + 1;
			String setFocusOnLineURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId
					+ "/elements/focus?datawindowName=items&fieldName=oe_order_item_id&row=" + itemIndex;
			logger.info("Set focus on specified line " + setFocusOnLineURI);
			logger.info("Item index to be targeted : " + itemIndex);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpPost setFocusOnLineRequest = new HttpPost(setFocusOnLineURI);

				// Set request headers
				setFocusOnLineRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				setFocusOnLineRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				setFocusOnLineRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse setFocusOnLineResponse = httpClient1.execute(setFocusOnLineRequest);
				String setFocusOnLineResponseBody = EntityUtils.toString(setFocusOnLineResponse.getEntity());
				logger.info("Set focus on Window Response :" + setFocusOnLineResponseBody);

			} catch (Exception e) {
				logger.error("Error Setting focus on line :: " + e.getMessage());
			}

			// ---------------- Get Row number of specified line ---------------------------
			// This API will give the active row number of the item we had set focus on
			String getRowNumberOfLineURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId
					+ "/elements/activerow?datawindowName=items";
			logger.info("Get row number on specified line " + getRowNumberOfLineURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpGet getRowNumberOfLineRequest = new HttpGet(getRowNumberOfLineURI);

				// Set request headers
				getRowNumberOfLineRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				getRowNumberOfLineRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				getRowNumberOfLineRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse getRowNumberOfLineResponse = httpClient1.execute(getRowNumberOfLineRequest);
				String getRowNumberOfLineResponseBody = EntityUtils.toString(getRowNumberOfLineResponse.getEntity());
				logger.info("Get row number of line Response :" + getRowNumberOfLineResponseBody);

			} catch (Exception e) {
				logger.error("Error getting active row of  line :: " + e.getMessage());
			}

			// --------------- Select page of window ------------------------------------
			// This API will select the sales history tab to find the row from which to link
			String selectPageOfWindowURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId
					+ "/elements/select?pageName=tabpage_saleshistory";
			logger.info("Select page of window URI" + setFocusOnLineURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpPost selectPageOfWindowRequest = new HttpPost(selectPageOfWindowURI);

				// Set request headers
				selectPageOfWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				selectPageOfWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				selectPageOfWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse selectPageOfWindowResponse = httpClient1.execute(selectPageOfWindowRequest);
				String selectPageOfWindowResponseBody = EntityUtils.toString(selectPageOfWindowResponse.getEntity());
				logger.info("Select page of window Response :" + selectPageOfWindowResponseBody);

			} catch (Exception e) {
				logger.error("Error selecting page of saleshistory window :: " + e.getMessage());
			}

			// ----------------- Get elements state--------------
			// This API will return the total no. rows of the saleshistory
			String getElementsStateURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/grid/" + windowId
					+ "/elements/state?dwName=tabpage_saleshistory";
			logger.info("Get elements state URI" + getElementsStateURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpGet getElementsStateRequest = new HttpGet(getElementsStateURI);

				getElementsStateRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				getElementsStateRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				getElementsStateRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse getElementsStateResponse = httpClient1.execute(getElementsStateRequest);
				String getElementsStateResponseBody = EntityUtils.toString(getElementsStateResponse.getEntity());
				logger.info("Get elements of state Response :" + getElementsStateResponseBody);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(getElementsStateResponseBody);

				// Extract TotalRows value
				totalRows = jsonNode.path("DataInformation").path("TABPAGE_SALESHISTORY.tabpage_saleshistory")
						.path("TotalRows").asInt();

				logger.info("Total rows : " + totalRows);
			} catch (Exception e) {
				logger.error("Error getting element state of saleshistory window :: " + e.getMessage());
			}

			// ---------- Get row to link from saleshistory -----------------
			// This API will return the saleshistory details , from which we select the row

			String getRowToLinkURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/grid/" + windowId
					+ "/elements/state?dwName=tabpage_saleshistory&activeRows=1-" + totalRows;
			logger.info("Get row to link URI" + getRowToLinkURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpGet getRowToLinkRequest = new HttpGet(getRowToLinkURI);

				getRowToLinkRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				getRowToLinkRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				getRowToLinkRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse getRowToLinkResponse = httpClient1.execute(getRowToLinkRequest);
				String getRowToLinkResponseBody = EntityUtils.toString(getRowToLinkResponse.getEntity());
				logger.info("Get row to linkResponse :" + getRowToLinkResponseBody);

				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(getRowToLinkResponseBody);

				JsonNode dataNode = rootNode.path("Data").path("TABPAGE_SALESHISTORY.tabpage_saleshistory");

				for (JsonNode objNode : dataNode) {
					if (objNode.has("invoice_no")
							&& objNode.get("invoice_no").asText().equals(invoiceToLink.toString())) {
						indexOfInvNo = objNode.get("_internalrowindex").asInt();
						logger.info("Index of Invoice no to link : " + indexOfInvNo);
						break;
					}
				}

			} catch (Exception e) {
				logger.error("Error getting row of saleshistory window to link :: " + e.getMessage());
			}

			// --------------- Set focus on row to link ------------------------------------
			// This will set focus on the index we got from previous API
			String setFocusToLinkURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/window/" + windowId
					+ "/elements/focus?datawindowName=tabpage_saleshistory&fieldName=invoice_no&row=" + indexOfInvNo;
			logger.info("Set focus to link URI" + setFocusToLinkURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpPost setFocusToLinkRequest = new HttpPost(setFocusToLinkURI);

				// Set request headers
				setFocusToLinkRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				setFocusToLinkRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				setFocusToLinkRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				CloseableHttpResponse setFocusToLinkResponse = httpClient1.execute(setFocusToLinkRequest);
				String setFocusToLinkResponseBody = EntityUtils.toString(setFocusToLinkResponse.getEntity());
				logger.info("Set focus on row to link response :" + setFocusToLinkResponseBody);

			} catch (Exception e) {
				logger.error("Error setting focus on the row to link :: " + e.getMessage());
			}

			// --------- Run tool on sales history --------------------------------
			// This will link the selected invoice with selected line
			String runToolOnWindowURI = masterTenant.getSubdomain() + "/uiserver0/ui/full/v2/window/tools";
			logger.info("Run tool window URI" + runToolOnWindowURI);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpPost runToolOnWindowRequest = new HttpPost(runToolOnWindowURI);
				String jsonBody = "{" + "\"DatawindowName\": \"tabpage_saleshistory\","
						+ "\"FieldName\": \"invoice_no\"," + "\"Row\": " + indexOfInvNo + "," + "\"Text\": null,"
						+ "\"ToolName\": \"m_linktothisrmaline\"," + "\"WindowId\": \"" + windowId + "\"" + "}";
				// Set request headers
				runToolOnWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				runToolOnWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				runToolOnWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				StringEntity entity = new StringEntity(jsonBody);
				runToolOnWindowRequest.setEntity(entity);
				CloseableHttpResponse runToolOnWindowResponse = httpClient1.execute(runToolOnWindowRequest);
				String runToolOnWindowResponseBody = EntityUtils.toString(runToolOnWindowResponse.getEntity());
				logger.info("Run tool window response :" + runToolOnWindowResponseBody);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(runToolOnWindowResponseBody);

				JsonNode messagesNode = rootNode.get("Messages");
				if (messagesNode != null && messagesNode.isArray() && messagesNode.size() > 0) {
					JsonNode firstMessageNode = messagesNode.get(0);
					JsonNode typeNode = firstMessageNode.get("Text");
					if (typeNode != null) {
						invoiceLinkingError = typeNode.asText();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// -----Verification of whether the invoice is linked or not

			// Set focus on which row to check for linking

			String setFocusForVerification = masterTenant.getSubdomain() + "/uiserver0/ui/full/v2/data/row";
			logger.info("URI to check focus for verification of link invoice line : " + setFocusForVerification);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpPut setFocusRequest = new HttpPut(setFocusForVerification);

				String pageName = "TP_ITEMS";
				String datawindowName = "items";

				String jsonRequestBody = "{" + "\"DatawindowName\": \"" + datawindowName + "\"," + "\"PageName\": \""
						+ pageName + "\"," + "\"Row\": " + itemIndex + "," + "\"WindowId\": \"" + windowId + "\"" + "}";

				setFocusRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				setFocusRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				setFocusRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
				StringEntity entity = new StringEntity(jsonRequestBody);
				setFocusRequest.setEntity(entity);
				CloseableHttpResponse setFocusResponse = httpClient1.execute(setFocusRequest);
				String setFocusResponseBody = EntityUtils.toString(setFocusResponse.getEntity());
				logger.info("Set Focus response :" + setFocusResponseBody);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(setFocusResponseBody);

				boolean success = rootNode.get("Success").asBoolean();

				if (success) {
					logger.info("Set Focus was successful.");
				} else {
					logger.error("Set Focus for verification failed.");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// check whether button is enabled or not
			String checkButtonEnabling = masterTenant.getSubdomain() + "/uiserver0/api/ui/emawindow/" + windowId
					+ "/elements/tools?datawindowName=items&fieldName=item_desc";
			logger.info("URI to check whether show invoice link button is enabled  : " + checkButtonEnabling);
			try {
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpGet getButtonEnableRequest = new HttpGet(checkButtonEnabling);

				getButtonEnableRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				getButtonEnableRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				getButtonEnableRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

				CloseableHttpResponse buttonResponse = httpClient1.execute(getButtonEnableRequest);
				String buttonResponseBody = EntityUtils.toString(buttonResponse.getEntity());
				logger.info("Grid response :" + buttonResponseBody);

				JSONObject jsonResponse = new JSONObject(buttonResponseBody);
				JSONArray resultArray = jsonResponse.getJSONArray("Result");

				if (resultArray.length() >= 10) {
					JSONObject tenthObject = resultArray.getJSONObject(9);
					isEnabled = tenthObject.getBoolean("Enabled");
					logger.info("Is Show linked invoice line enabled: " + isEnabled);
				} else {
					logger.info("Object at 10th position not found in the response.");
				}
				logger.info("THIS IS ENABLED ? >> " + isEnabled);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (isEnabled) {

				// Run the tool to see linked invoice
				// Line for
				// Verification-------------------------------------------------------------

				String runToolonItemWindow = masterTenant.getSubdomain() + "/uiserver0/ui/full/v2/window/tools";
				logger.info("URI to run tool on item window for verification of link invoice line : "
						+ runToolonItemWindow);
				try {
					CloseableHttpClient httpClient1 = HttpClients.custom()
							.setSSLContext(
									SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
					HttpPost runToolOnItemRequest = new HttpPost(runToolonItemWindow);

					String datawindowName = "items";
					String pageName = "m_showlinkedinvoiceline";

					String jsonRequestBody = "{" + "\"DatawindowName\": \"" + datawindowName + "\","
							+ "\"FieldName\": \"item_desc\"," + "\"Row\": " + itemIndex + "," + "\"Text\": null," +

							"\"ToolName\": \"" + pageName + "\"," + "\"WindowId\": \"" + windowId + "\"" + "}";

					runToolOnItemRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					runToolOnItemRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
					runToolOnItemRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

					StringEntity entity = new StringEntity(jsonRequestBody);
					runToolOnItemRequest.setEntity(entity);
					CloseableHttpResponse runToolOnItemResponse = httpClient1.execute(runToolOnItemRequest);
					String runToolResponseBody = EntityUtils.toString(runToolOnItemResponse.getEntity());
					logger.info("Run tool on item response :" + runToolResponseBody);

					JSONObject jsonResponse = new JSONObject(runToolResponseBody);

					JSONArray eventsArray = jsonResponse.getJSONArray("Events");

					if (eventsArray.length() > 0) {

						JSONObject firstEvent = eventsArray.getJSONObject(0);
						JSONObject eventData = firstEvent.getJSONObject("EventData");

						if (eventData.has("windowid")) {
							childWindowId = eventData.getString("windowid");
							logger.info(
									"This is childwindowId while running I-API for verification : " + childWindowId);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				// Get the grid
				// quickly----------------------------------------------------------------------------

				String getGrid = masterTenant.getSubdomain() + "/uiserver0/api/ui/emawindow/" + childWindowId
						+ "/preference/grid.columns.locked/object/_dw_1";
				logger.info("URI to get grid data : " + getGrid);
				try {
					CloseableHttpClient httpClient1 = HttpClients.custom()
							.setSSLContext(
									SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
					HttpGet getGridRequest = new HttpGet(getGrid);

					getGridRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					getGridRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
					getGridRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

					CloseableHttpResponse gridResponse = httpClient1.execute(getGridRequest);
					String gridResponseBody = EntityUtils.toString(gridResponse.getEntity());
					logger.info("Grid response :" + gridResponseBody);

				} catch (Exception e) {
					e.printStackTrace();
				}

				// Get active rows in that data of
				// invoice----------------------------------------------------------
				String getActiveRows = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/grid/" + childWindowId
						+ "/elements/state?dwName=_dw_1";

				logger.info("URI to get active rows data : " + getActiveRows);
				try {
					CloseableHttpClient httpClient1 = HttpClients.custom()
							.setSSLContext(
									SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
					HttpGet getActiveRowRequest = new HttpGet(getActiveRows);

					getActiveRowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					getActiveRowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
					getActiveRowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

					CloseableHttpResponse getActiveRowResponse = httpClient1.execute(getActiveRowRequest);
					String getActiveResponseBody = EntityUtils.toString(getActiveRowResponse.getEntity());
					logger.info("Get active rows response body :" + getActiveResponseBody);

					JSONObject jsonResponse = new JSONObject(getActiveResponseBody);

					JSONObject dataInformation = jsonResponse.getJSONObject("DataInformation");

					if (dataInformation.has("_dw_1")) {
						JSONObject dw1Object = dataInformation.getJSONObject("_dw_1");
						if (dw1Object.has("TotalRows")) {
							totalVerificationRows = dw1Object.getInt("TotalRows");
							logger.info("TotalRows in Invoice data view in sandbox is : " + totalVerificationRows);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				// Get Invoice data and Cross Verify to check invoice is linked or
				// not-------------------------------------

				String getInvoicedRows = masterTenant.getSubdomain() + "/uiserver0/ui/full/v1/grid/" + childWindowId
						+ "/elements/state?dwName=_dw_1&activeRows=1-" + totalVerificationRows;

				logger.info("URI to get invoiced rows data : " + getInvoicedRows);
				try {
					CloseableHttpClient httpClient1 = HttpClients.custom()
							.setSSLContext(
									SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
					HttpGet getInvoiceRowRequest = new HttpGet(getInvoicedRows);

					getInvoiceRowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					getInvoiceRowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
					getInvoiceRowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

					CloseableHttpResponse getInvoiceRowResponse = httpClient1.execute(getInvoiceRowRequest);
					String getInvoiceResponseBody = EntityUtils.toString(getInvoiceRowResponse.getEntity());
					logger.info("Get invoice rows response body :" + getInvoiceResponseBody);

					JSONObject jsonResponse = new JSONObject(getInvoiceResponseBody);

					JSONArray dw1Array = jsonResponse.getJSONObject("Data").getJSONArray("_dw_1");

					if (dw1Array.length() > 0) {
						JSONObject dw1Object = dw1Array.getJSONObject(0);
						Integer invoiceNumber = dw1Object.optInt("invoice_no");

						if (invoiceNumber.equals(invoiceToLink)) {
							logger.info(
									"------INVOICE LINKING WAS SUCCESSFUL FOR THE RMA NUMBER------- : : : " + rmaNo);
						}

						else {
							String recipient = "continuum@bytesfarms.com";
							String template = TemplateRenderrer.getInvoice_Link_Failed_Template();
							String subject = "Invoice Linking Failed For RMA : " + rmaNo + " for the item : "
									+ itemName;
							HashMap<String, String> map = new HashMap<>();
							map.put("rmaNumber", rmaNo);
							map.put("lineItem", itemName);

							try {
								emailSender.sendEmail(recipient, template, subject, map);
								logger.info("Email sent to " + recipient + " due to failed invoice linking.");
							} catch (MessagingException e) {
								logger.error("Failed to send email: " + e.getMessage(), e);
							}

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				// Press Ok and come out of that Child
				// Window-----------------------------------------------------

				String pressOk = masterTenant.getSubdomain() + "/uiserver0/api/ui/emawindow/" + childWindowId
						+ "/tools/cb_ok";
				logger.info("URI to press OK on childWindow : " + pressOk);
				try {
					CloseableHttpClient httpClient1 = HttpClients.custom()
							.setSSLContext(
									SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
					HttpPut pressOKRequest = new HttpPut(pressOk);

					pressOKRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
					pressOKRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
					pressOKRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

					CloseableHttpResponse pressOkResponse = httpClient1.execute(pressOKRequest);
					String pressOKResponseBody = EntityUtils.toString(pressOkResponse.getEntity());
					logger.info("Press OK response :" + pressOKResponseBody);

					JSONObject jsonResponse = new JSONObject(pressOKResponseBody);

					boolean isSuccess = jsonResponse.getBoolean("Success");

					if (isSuccess) {
						logger.info("We have Pressed OK Button now we can save window. ");
					} else {
						logger.info(
								"Unsuccessfull OK Window Response, Inv Link Failure because we couldn't save window now! ");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			else {
				String recipient = "continuum@bytesfarms.com";
				String template = TemplateRenderrer.getInvoice_Link_Failed_Template();
				String subject = "Invoice Linking Failed For RMA : " + rmaNo + " for the item : " + itemName;
				HashMap<String, String> map = new HashMap<>();
				map.put("rmaNumber", rmaNo);
				map.put("lineItem", itemName);
				map.put("invoiceNumber", invoiceToLink.toString());
				map.put("Tenant", masterTenant.getDbName());
				map.put("invoiceLinkingError", invoiceLinkingError);

				try {
					emailSender.sendEmail(recipient, template, subject, map);
					logger.info("Email sent to " + recipient + " due to failed invoice linking.");
				} catch (MessagingException e) {
					logger.error("Failed to send email: " + e.getMessage(), e);
				}
			}

			// Continuation to further process after checking its linked or not

			// --------- Update return quantity after linking ----------------------------
			// This will update the return quantity in the ERP equals to the quantity we had
			// return
			try {
				logger.info("Update line item in RMA");

				CloseableHttpClient httpClient12 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				String quantityUpdateURI = String.format("" + masterTenant.getSubdomain()
						+ "/uiserver0/ui/full/v2/data/data?dw=items&fn=unit_quantity&r=1&tn=TP_ITEMS&wn=w_rma_entry_sheet&wid=%s",
						masterTenant.getSubdomain(), windowId);

				logger.info(quantityUpdateURI);

				URI quantityUpdateURL = new URIBuilder(quantityUpdateURI).build();

				HttpPut runToolOnWindowRequest = new HttpPut(quantityUpdateURL);
				runToolOnWindowRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				runToolOnWindowRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				runToolOnWindowRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

				JSONObject requestBody1 = new JSONObject();
				requestBody1.put("DatawindowName", "items");
				requestBody1.put("FieldName", "unit_quantity");
				requestBody1.put("Value", quantity);
				requestBody1.put("WindowId", windowId);
				requestBody1.put("Row", itemIndex);

				// Set the request body
				StringEntity requestEntity = new StringEntity(requestBody1.toString());
				logger.info("This is request body for update item: " + requestEntity);
				runToolOnWindowRequest.setEntity(requestEntity);

				CloseableHttpResponse runToolOnWindowResponse = httpClient12.execute(runToolOnWindowRequest);
				HttpEntity entity11 = runToolOnWindowResponse.getEntity();
				String runToolOnWindowResponseBody = EntityUtils.toString(entity11);
				logger.info("Update line item in quantity : " + runToolOnWindowResponseBody);

			} catch (Exception e) {
				logger.error("Error Run Tool on Window::" + e.getMessage());
			}

			// ----------------Save Window ---------------------------------
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
				logger.error("Error saveWindow::" + e.getMessage());
			}

			// ---------------- End Session ------------------------------------
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

		}
		return b;

	}

	// Invoice Linking inner method.
	public Integer getIndexOfItem(String rmaNo, MasterTenant masterTenantObject, ReturnOrderItem item)
			throws Exception {

		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(rmaNo);
		ReturnOrder returnOrder = findByRmaOrderNo.get();
		String orderNo = returnOrder.getOrderNo();

		Integer indexForInvoice = 0;

		String itemId = item.getItemName();
		Integer foundInvoiceNumber = item.getInvoiceNo();
		if (foundInvoiceNumber == null) {
			return 0;
		}
		String foundInvoiceNumberString = foundInvoiceNumber.toString();
		Set<SerialData> serialNo = item.getSerialData();

		String rmaDetailsUrl = masterTenantObject.getSubdomain() + rmaGetEndPoint + "/get";
		logger.info("Transaction URL to get serial numbers : " + rmaDetailsUrl);
		String requestBody2 = constructFirstApiRequestBody(rmaNo);
		String accessToken2 = p21TokenServiceImpl.findToken(masterTenantObject);

		CloseableHttpClient httpClient1 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		HttpPost httpPost = new HttpPost(rmaDetailsUrl);
		httpPost.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken2);
		httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		httpPost.setEntity(new StringEntity(requestBody2));

		CloseableHttpResponse response2 = httpClient1.execute(httpPost);
		String responseBody2 = EntityUtils.toString(response2.getEntity());

		JsonNode rootNode1 = objectMapper.readTree(responseBody2);
		JsonNode itemsNode = rootNode1.path("Transactions").get(0).path("DataElements").get(70).path("Rows");

		Integer i = 1;

		if (itemsNode != null && itemsNode.isArray()) {
			for (JsonNode singleItem : itemsNode) {
				JsonNode editsNode = singleItem.path("Edits");
				if (editsNode.isArray()) {
					for (JsonNode edit : editsNode) {
						JsonNode valueNode = edit.path("Value");
						if (valueNode.isTextual()) {
							String value = valueNode.asText();
							if (value.equals(foundInvoiceNumberString)) {
								System.out.println("This is index for the item : " + i);
								System.out.println("This is the item : " + itemId);

								indexForInvoice = i;
								break;
							}
						}
					}
				}
				i++;
			}
		}

		return indexForInvoice;
	}

	private String constructFirstApiRequestBody(String rmaNo) {
		return "{ \"ServiceName\":\"RMA\", " + "\"TransactionStates\":[{ \"DataElementName\":\"TABPAGE_1.order\", "
				+ "\"Keys\":[{ \"Name\":\"order_no\", \"Value\":" + rmaNo + " }] }], " + "\"UseCodeValues\":true }";
	}

}