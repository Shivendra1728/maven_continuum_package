package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.StoreRepository;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21OrderLineService;
import com.di.integration.p21.transaction.SerialData;
import com.di.integration.p21.transaction.SerialDataResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21OrderLineServiceImpl implements P21OrderLineService {

	private static final Logger logger = LoggerFactory.getLogger(P21OrderLineServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_LINE)
	String DATA_API_ORDER_LINE;

	@Value(IntegrationConstants.ERP_DATA_API_SERIAL_LINE)
	String DATA_API_SERIAL_LINE;

	@Value(IntegrationConstants.ERP_ORDER_LINE_SELECT_FIELDS)
	String ORDER_LINE_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_DATA_API_INVOICE_LINE_VIEW)
	String INVOICE_LINE_VIEW;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	P21OrderLineItemMapper p21orderLineItemMapper;

	@Autowired
	StoreDTO storeDTO;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	LocalDate localDate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters,
			MasterTenant masterTenantObject, int totalItem, String invoiceNo)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception {

		List<OrderItemDTO> orderItemDTOList = p21orderLineItemMapper.convertP21OrderLineObjectToOrderLineDTO(
				getOrderLineData(orderSearchParameters, totalItem), orderSearchParameters, invoiceNo);

		List<OrderItemDTO> updatedOrderItemDTOList = new ArrayList<>();

		// Here we set invoice Number against items

		long idCounter = 1;

		for (OrderItemDTO orderItemDTO : orderItemDTOList) {
			String orderNo = orderItemDTO.getOrderNo();
			String itemId = orderItemDTO.getPartNo();

			// let's hit the api
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			MasterTenant masterTenant;
			if (masterTenantObject == null) {
				String tenantId = httpServletRequest.getHeader("tenant");
				masterTenant = masterTenantRepository.findByDbName(tenantId);
			} else {
				masterTenant = masterTenantObject;
			}

			String invoiceDetailsURL = null;
			try {
				URIBuilder uriBuilder = new URIBuilder(
						masterTenant.getSubdomain() + DATA_API_BASE_URL + INVOICE_LINE_VIEW);
				uriBuilder.addParameter("$filter", "order_no eq '" + orderNo + "' and item_id eq '" + itemId + "'");
				invoiceDetailsURL = uriBuilder.build().toString();
			} catch (Exception e) {

				e.printStackTrace();
			}

			logger.info("URL to get Invoice number against Item : " + invoiceDetailsURL);
			String accessToken = p21TokenServiceImpl.findToken(masterTenantObject);

			HttpGet httpGet = new HttpGet(invoiceDetailsURL);
			httpGet.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
			httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			CloseableHttpResponse response = httpClient.execute(httpGet);
			String responseBody = EntityUtils.toString(response.getEntity());

			JsonNode responseNode = objectMapper.readTree(responseBody);
			logger.info("responseNode : " + responseNode);
			// Process response data
			JsonNode valueNode = responseNode.get("value");

			if (valueNode == null || valueNode.isEmpty()) {
				String dispositionDetailsURL = null;
				logger.info("Disposition code");
				CloseableHttpClient httpClient1 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

				try {
					URIBuilder uriBuilder = new URIBuilder(
							masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_API_ORDER_LINE);
					uriBuilder.addParameter("$filter", "order_no eq '" + orderNo + "' and item_id eq '" + itemId + "'");
					dispositionDetailsURL = uriBuilder.build().toString();
				} catch (Exception e) {

					e.printStackTrace();
				}

				logger.info("URL to get Disposition against Item : " + dispositionDetailsURL);
//				String accessToken1 = p21TokenServiceImpl.findToken(masterTenantObject);
				HttpGet httpGet1 = new HttpGet(dispositionDetailsURL);
				httpGet1.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
				httpGet1.setHeader(HttpHeaders.ACCEPT, "application/json");
				httpGet1.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

				CloseableHttpResponse getLineResponse = httpClient1.execute(httpGet1);
				String getLineResponseBody = EntityUtils.toString(getLineResponse.getEntity());

				logger.info("Disposition item response : " + getLineResponseBody);

				int statusCode = getLineResponse.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					logger.info(getLineResponseBody);
					if (getLineResponseBody != null && getLineResponseBody.contains("\"disposition\":\"D\"")) {
						OrderItemDTO updatedOrderItemDTO = new OrderItemDTO(orderItemDTO);
						updatedOrderItemDTO.setDropShip(true);
						updatedOrderItemDTOList.add(updatedOrderItemDTO);
					}

				}

			}

			for (JsonNode itemNode : valueNode) {
				String invoiceNoFromResponse = itemNode.get("invoice_no").asText();
				long invoiceNoLong = itemNode.get("invoice_no").asLong();

				double qtyShipped = Double.parseDouble(itemNode.get("qty_shipped").asText());

				if (orderNo.equals(itemNode.get("order_no").asText())
						&& itemId.equals(itemNode.get("item_id").asText())) {

					OrderItemDTO updatedOrderItemDTO = new OrderItemDTO(orderItemDTO);

					updatedOrderItemDTO.setId(idCounter++);

					updatedOrderItemDTO.setInvoiceNo(invoiceNoFromResponse);

					updatedOrderItemDTO.setQuantity((int) qtyShipped);

					updatedOrderItemDTOList.add(updatedOrderItemDTO);
				}
			}

		}
		logger.info("Updated order item list : " + updatedOrderItemDTOList);
		return updatedOrderItemDTOList;
	}

	private String getOrderLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareOrderLineURI(orderSearchParameters, totalItem);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);
	}

	private URI prepareOrderLineURI(OrderSearchParameters orderSearchParameters, int totalItem)
			throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

//		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
//			// filter.append("original_invoice_no eq '" +
//			// orderSearchParameters.getInvoiceNo() + "'");
//
//			filter.append(IntegrationConstants.INVOICE_NO).append(" ")
//					.append(IntegrationConstants.CONDITION_EQ).append(" '").append(orderSearchParameters.getInvoiceNo())
//					.append("'");
//		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getOrderNo())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			// filter.append("order_no eq '" + orderSearchParameters.getOrderNo() + "'");

			filter.append(IntegrationConstants.ORDER_NO).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(orderSearchParameters.getOrderNo()).append("'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			// filter.append("customer_id eq " + orderSearchParameters.getCustomerId());

			filter.append(IntegrationConstants.CUSTOMER_ID).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" ").append(orderSearchParameters.getCustomerId());
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
			URI uri = new URI(masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_API_ORDER_LINE);
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
	public List<SerialData> getSerialNumbers(String orderNo, String lineNo) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		logger.info("RMA Reciept getSerialNumber Method called.....");

		// String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		URIBuilder uriBuilder = new URIBuilder(masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_API_SERIAL_LINE);
		uriBuilder.setParameter("$format", "json");
		uriBuilder.setParameter("$filter",
				"document_no eq " + URLEncoder.encode(orderNo, StandardCharsets.UTF_8.toString()) + " and line_no eq "
						+ URLEncoder.encode(lineNo, StandardCharsets.UTF_8.toString()));

		URI uri = uriBuilder.build();

		HttpGet request = new HttpGet(uri);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		ObjectMapper objectMapper = new ObjectMapper();
		SerialDataResponse serialDataResponse = objectMapper.readValue(EntityUtils.toString(entity),
				SerialDataResponse.class);
		List<SerialData> serialDataList = serialDataResponse.getValue();

		return serialDataList;
	}

	// Fetch items from invoice -

	public List<OrderItemDTO> getordersLineByInvoice(String invoiceNo, int totalItem) throws Exception {
		List<OrderItemDTO> orderItemDTOList = p21orderLineItemMapper.convertP21OrderLineObjectToOrderLineDTOForInvoice(
				getOrderLineDataFromInvoice(invoiceNo, totalItem), invoiceNo);

		logger.info("This is OrderItem DTO List " + orderItemDTOList);

		return orderItemDTOList;
	}

	private String extractOtherChargeValue(HttpEntity entity) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(entity.getContent());

			JsonNode valueNode = rootNode.get("value").get(0);

			String otherChargeValue = valueNode.get("other_charge").asText();

			return otherChargeValue;
		} catch (Exception e) {
			logger.error("Error while extracting Othercharge value: " + e.getMessage());
			return null;
		}
	}

	private String getOrderLineDataFromInvoice(String invoiceNo, int totalItem) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareOrderLineURIForInvoice(invoiceNo, totalItem);
		logger.info("This is URL to search items against invoice : : " + fullURI);
		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);
	}

	private URI prepareOrderLineURIForInvoice(String invoiceNo, int totalItem) {
		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(invoiceNo)) {
			filter.append(IntegrationConstants.INVOICE_NO).append(" ").append(IntegrationConstants.CONDITION_EQ)
					.append(" '").append(invoiceNo).append("'");
		}

		String tenentId = httpServletRequest.getHeader("tenant");

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$filter=" + encodedFilter;
			URI uri = new URI(masterTenant.getSubdomain() + DATA_API_BASE_URL + INVOICE_LINE_VIEW + "?" + query);
			logger.info("This is URL to search items against invoice: {}", uri);
			return uri;
		} catch (Exception e) {
			logger.error("An error occurred while preparing the order line URI: {}", e.getMessage());
			return null;
		}
	}

}
