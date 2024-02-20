package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ClientConfig;
import com.continuum.tenant.repos.repositories.ClientConfigRepository;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.StoreRepository;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21ContactMapper;
import com.di.commons.p21.mapper.P21InvoiceMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21OrderServiceImpl implements P21OrderService {

	private static final Logger logger = LoggerFactory.getLogger(P21OrderServiceImpl.class);
	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_P21_OE_ORDER_VIEW)
	String DATA_OE_ORDER;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_VIEW)
	String DATA_API_ORDER_VIEW;

	// @Value("${erp.token}") //property also commented
	// String TOKEN;

	@Value(IntegrationConstants.ERP_ORDER_SELECT_FIELDS)
	String ORDER_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	String UI_SERVER;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	P21OrderMapper p21OrderMapper;

	@Autowired
	P21OrderLineServiceImpl p21OrderLineServiceImpl;

	@Autowired
	P21ContactMapper p21ContactMapper;
	@Autowired
	StoreDTO storeDTO;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	StoreRepository storeRepository;

	LocalDate localDate;

	@Autowired
	ClientConfigRepository clientConfigRepository;

	ClientConfig clientConfig;

	@Autowired
	P21InvoiceMapper p21InvoiceMapper;

	@Autowired
	P21InvoiceServiceImpl p21InvoiceServiceImpl;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) throws Exception {
		List<OrderDTO> orderDTOList = new ArrayList<>();
		List<OrderItemDTO> orderItemDTOList = new ArrayList<>();

		if (!isNotNullAndNotEmpty(orderSearchParameters.getOrderNo())
				&& isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			int totalItem = 1;
			List<OrderItemDTO> invoiceItemDTOList = p21InvoiceMapper.mapP21InvoiceResponseToData(
					p21InvoiceServiceImpl.getInvoiceLineData(orderSearchParameters, totalItem)); // Invoice header
			if (invoiceItemDTOList.size() > 0) {
				orderSearchParameters.setOrderNo(invoiceItemDTOList.get(0).getOrderNo());
				orderDTOList = getAllOrdersBySearch(orderSearchParameters, orderItemDTOList);
			}
		} else {
			orderDTOList = getAllOrdersBySearch(orderSearchParameters, orderItemDTOList);
		}

		if (orderDTOList.isEmpty() || orderDTOList.get(0).getOrderItems().isEmpty()) {
			OrderDTO specialOrderDTO = new OrderDTO();
			specialOrderDTO.setMessage("No items in this order are invoiced or drop shipped.");
			return Collections.singletonList(specialOrderDTO);
		}

		// First we parse carrier id
		String orderNumber = orderDTOList.get(0).getOrderNo();
		String CarrierIdData = getCarrierData(orderNumber);
		String carrierId = parseCarrierId(CarrierIdData);

		// Secondly we fetch name from carrier now
		String CarrierNameData = getCarrierNameData(carrierId);
		String CarrierName = parseCarrierName(CarrierNameData);
		orderDTOList.get(0).setCarrierName(CarrierName);

		// Map to store parent items and their corresponding child items
		Map<Long, List<OrderItemDTO>> parentChildMap = new HashMap<>();

		for (OrderDTO orderDTO : orderDTOList) {
			List<OrderItemDTO> items = orderDTO.getOrderItems();
			logger.info("Length of items list: " + items.size());

			for (OrderItemDTO item : items) {
				logger.info("This is item : " + item.getItemName());

				Long parentOeLineUid = item.getParentLineId();
				logger.info("This is parentOeLineUid : " + parentOeLineUid);
				Long oeLineUid = item.getOrderLineId();
				logger.info("This is oeLineUid : " + oeLineUid);

				// Add the item to its parent's list of child items
				if (parentOeLineUid != null && parentOeLineUid != 0) {
					parentChildMap.computeIfAbsent(parentOeLineUid, k -> new ArrayList<>()).add(item);
				}

				// Set the innerItems of the item if it's a parent item
				if (parentOeLineUid == null || parentOeLineUid == 0) {
					List<OrderItemDTO> childItems = parentChildMap.getOrDefault(oeLineUid, Collections.emptyList());
					item.setInnerItems(childItems);
				}
			}

		}

		// Remove child items from the orderItems list
		List<OrderItemDTO> orderItems = orderDTOList.get(0).getOrderItems();
		for (int i = orderItems.size() - 1; i >= 0; i--) {
			if (orderItems.get(i).getParentLineId() != null && orderItems.get(i).getParentLineId() != 0) {
				orderItems.remove(i);
			}
		}

		return orderDTOList;
	}

	private List<OrderDTO> getAllOrdersBySearch(OrderSearchParameters orderSearchParameters,
			List<OrderItemDTO> orderItemDTOList)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception {
		List<OrderDTO> orderDTOList = new ArrayList<>();
		orderDTOList = p21OrderMapper.convertP21OrderObjectToOrderDTO(getOrderData(orderSearchParameters));

		if (orderDTOList.size() > 0) {
			clientConfig = clientConfigRepository.findByErpCompanyId(orderDTOList.get(0).getCompanyId());
		}
		for (OrderDTO orderDTO : orderDTOList) {
			int totalItem = -1; // fetch all items in case of -1
			OrderSearchParameters orderSearchParams = new OrderSearchParameters();
			orderSearchParams.setOrderNo(orderDTO.getOrderNo());
			// code here

			if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
				if (orderItemDTOList.size() == 0) {
					orderItemDTOList = p21OrderLineServiceImpl
							.getordersLineByInvoice(orderSearchParameters.getInvoiceNo(), totalItem);
				}
			} else {
				// If invoice number is not present, fetch items using the existing API
				if (orderItemDTOList.size() == 0) {
					orderItemDTOList = p21OrderLineServiceImpl.getordersLineBySearchcriteria(orderSearchParams, null,
							totalItem, orderSearchParams.getInvoiceNo());
				}
			}

			if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
				orderItemDTOList = orderItemDTOList.stream().filter(item -> item.getInvoiceNo() != null)
						.collect(Collectors.toList());
			}
			orderDTO.setOrderItems(orderItemDTOList);

			orderDTO.setContactDTO(
					p21ContactMapper.convertP21ContactObjectToContactDTO(getContactData(orderDTO.getContactEmailId())));

			if (clientConfig != null && clientConfig.getReturnPolicyPeriod() != null) {
				localDate = LocalDate.now().minusDays(clientConfig.getReturnPolicyPeriod());
				logger.info("Return policy period: " + clientConfig.getReturnPolicyPeriod());

				if (localDate != null) {

					for (OrderItemDTO orderItemDTO : orderItemDTOList) {
						if (orderItemDTO.getInvoiceDate() != null) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

							LocalDate invoiceDate = LocalDate.parse(orderItemDTO.getInvoiceDate(), formatter);
							int comparisonResult = invoiceDate.compareTo(localDate);
							if (comparisonResult < 0) {
								orderItemDTO.setEligibleForReturn(false);
								logger.info("invoiceDate is before Return policy period");
							} else if (comparisonResult >= 0) {
								orderItemDTO.setEligibleForReturn(true);
								System.out.println("invoiceDate is after Return policy period");
							}
						}
					}
				}
			}

		}

		return orderDTOList;
	}

	private String parseCarrierName(String carrierNameData) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(carrierNameData);

			JsonNode editsArray = rootNode.path("Transactions").path(0).path("DataElements").path(0).path("Rows")
					.path(0).path("Edits");

			for (JsonNode editNode : editsArray) {
				JsonNode nameNode = editNode.path("Name");
				JsonNode valueNode = editNode.path("Value");

				if ("name".equalsIgnoreCase(nameNode.asText())) {
					return valueNode.asText();
				}
			}

			return "Carrier Name not found";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error parsing carrier name";
		}
	}

	private String getCarrierNameData(String carrierId) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareCarrierNameURI();

		HttpPost request = new HttpPost(fullURI);
		logger.info("Carrier NAME DATA URL" + request);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));
		request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request.setHeader(HttpHeaders.ACCEPT, "application/json");
		String requestBody = "{\n" + "  \"ServiceName\": \"Carrier\",\n" + "  \"TransactionStates\": [\n" + "    {\n"
				+ "      \"DataElementName\": \"FORM.form\",\n" + "      \"Keys\": [\n" + "        {\n"
				+ "          \"Name\": \"id\",\n" + "          \"Value\": " + carrierId + "\n" + "        }\n"
				+ "      ]\n" + "    }\n" + "  ],\n" + "  \"UseCodeValues\": true\n" + "}";

		request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);

	}

	private URI prepareCarrierNameURI() {

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		try {
			URI uri = new URI(masterTenant.getSubdomain() + "/uiserver0/api/v2/transaction" + "/get");
			return uri;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}

	}

	private String getOrderData(OrderSearchParameters orderSearchParameters) throws Exception {

		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareOrderURI(orderSearchParameters);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);

	}

	private String getContactData(String email) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareContactURI(email);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);

		// Process the API response
		/*
		 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
		 * response.getBody(); System.out.println("API response: " + responseBody); }
		 * else { System.err.println("API call failed with status code: " +
		 * response.getStatusCodeValue()); }
		 */
	}

	private URI prepareContactURI(String email) {

		// p21_view_contacts?$select=&$filter=email_address eq
		// 'SOUSADA.SALINTHONE@AZZUR.COM'&$format=json

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		try {

			String filter = "email_address eq '" + email + "'";

			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter;

			URI uri = new URI(
					masterTenant.getSubdomain() + DATA_API_BASE_URL + IntegrationConstants.ENDPOINT_VIEW_CONTACTS);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			return fullURI;
		} catch (Exception e) {

			logger.error("An error occurred while preparing the contact URI: {}", e.getMessage());

		}
		return null;
	}

	private URI prepareOrderURI(OrderSearchParameters orderSearchParameters) {

		StringBuilder filter = new StringBuilder();
		if (!isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())
				&& isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			filter.append("mail_postal_code_a eq '" + orderSearchParameters.getZipcode() + "'");

		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append("customer_id eq " + orderSearchParameters.getCustomerId());

		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getPoNo())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append("po_number eq '" + orderSearchParameters.getPoNo() + "'");

		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append("mail_postal_code_a eq '" + orderSearchParameters.getZipcode() + "'");

		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getOrderNo())) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append("order_no eq '" + orderSearchParameters.getOrderNo() + "'");

		}

		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + ORDER_SELECT_FIELDS + "&$filter=" + encodedFilter
					+ "&$top=1&$orderby=order_date";

//			String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
			String tenentId = httpServletRequest.getHeader("tenant");

			MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

			URI uri = new URI(masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_API_ORDER_VIEW);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			logger.info("Filtering orders with order_date greater than or equal to: {}", localDate);
			logger.info("Current date: {}", LocalDate.now());

			return fullURI;
		} catch (Exception e) {

			logger.error("An error occurred while preparing the order URI: {}", e.getMessage());

		}
		return null;
	}

	private String getCarrierData(String orderNo) throws Exception {
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareCarrierURI(orderNo);

		HttpGet request = new HttpGet(fullURI);
		logger.info("Carrier DATA URL" + request);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);

	}

	private URI prepareCarrierURI(String orderNo) {
		StringBuilder filter = new StringBuilder();
		if (isNotNullAndNotEmpty(orderNo)) {
			filter.append("order_no eq '" + orderNo + "'");
		}
		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter;

//			String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

			String tenentId = httpServletRequest.getHeader("tenant");

			MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

			URI uri = new URI(masterTenant.getSubdomain() + DATA_API_BASE_URL + DATA_OE_ORDER);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			logger.info("Filtering Carrier ID with order_date greater than or equal to: {}", localDate);
			logger.info("Current date: {}", LocalDate.now());

			return fullURI;
		} catch (Exception e) {

			logger.error("An error occurred while preparing the Carrier URI: {}", e.getMessage());

		}

		return null;
	}

	private String parseCarrierId(String responseString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(responseString);

		JsonNode valueNode = rootNode.get("value");
		if (valueNode != null && valueNode.isArray() && valueNode.size() > 0) {
			JsonNode firstItem = valueNode.get(0);
			JsonNode carrierIdNode = firstItem.get("carrier_id");

			if (carrierIdNode != null && carrierIdNode.isTextual()) {
				return carrierIdNode.asText();
			}
		}

		return null;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

}