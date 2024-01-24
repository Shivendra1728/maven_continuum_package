package com.di.integration.p21.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.P21ProductItem;
import com.di.commons.helper.P21ProductItemHelper;
import com.di.commons.p21.mapper.P21ContactMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21ProductServiceImpl implements P21ProductService {

	private static final Logger logger = LoggerFactory.getLogger(P21ReturnOrderServiceImpl.class);

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String ERP_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_PRODUCT_API_BASE_URL)
	String ERP_PRODUCT_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_VIEW)
	String DATA_API_ORDER_VIEW;

	LocalDate localDate;

	@Value(IntegrationConstants.ERP_ORDER_SELECT_FIELDS)
	String ORDER_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Autowired
	P21OrderMapper p21OrderMapper;

	@Autowired
	P21ContactMapper p21ContactMapper;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public List<OrderDTO> getProductByProductId(String productId, String customerId) throws ParseException, Exception {
		List<OrderDTO> orderDTOList = new ArrayList<>();
//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		try {
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			String baseUri = masterTenant.getSubdomain() + ERP_BASE_URL + ERP_PRODUCT_API_BASE_URL;
			String filter = "item_id eq '" + productId + "'";
			URIBuilder uriBuilder = new URIBuilder(baseUri);
			uriBuilder.addParameter("$format", "json");
			uriBuilder.addParameter("$filter", filter);

			URI fullURI = uriBuilder.build();
			HttpGet request = new HttpGet(fullURI);

			try {
				request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));
				logger.info("Token : " + p21TokenServiceImpl.findToken(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String responseBody = EntityUtils.toString(entity);
			logger.info("response :" + responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			P21ProductItemHelper p21ProductLineItemHelper = objectMapper.readValue(responseBody,
					P21ProductItemHelper.class);

			List<P21ProductItem> p21ProductLineItems = p21ProductLineItemHelper.getValue();
			List<OrderItemDTO> orderItems = new ArrayList<>();
			OrderDTO orderDTO = new OrderDTO();

			for (P21ProductItem p21ProductItem : p21ProductLineItems) {
				OrderItemDTO orderItemDTO = new OrderItemDTO();
				orderItemDTO.setId(Long.parseLong(p21ProductItem.getInv_mast_uid()));
				orderItemDTO.setItemName(p21ProductItem.getItem_id());
				orderItemDTO.setDescription(p21ProductItem.getItem_desc());
				orderItemDTO.setQuantity(1);
				orderItemDTO.setAmount(new BigDecimal(p21ProductItem.getPrice1()));
				orderItemDTO.setSearchFrom("productId");
				orderItems.add(orderItemDTO);
			}

			if (orderItems == null || orderItems.isEmpty()) {

				throw new Exception("Invalid response:Please put correct Product Id.");
			}
			orderDTO.setOrderItems(orderItems);
//			orderDTO.setSearchFrom("productId");

			orderDTOList = p21OrderMapper.convertP21OrderObjectToOrderDTO(getOrderData(customerId));

			logger.info("This is Order DTO :: " + orderDTOList);

			orderDTO.setContactDTO(p21ContactMapper
					.convertP21ContactObjectToContactDTO(getContactData(orderDTOList.get(0).getContactEmailId())));
			if (!orderDTOList.isEmpty()) {
				OrderDTO existingOrderDTO = orderDTOList.get(0);
				existingOrderDTO.setOrderItems(orderItems);
				existingOrderDTO.setContactDTO(orderDTO.getContactDTO());

			} else {
				orderDTOList.add(orderDTO);
			}

		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		return orderDTOList;
	}

	private String getOrderData(String customerId) throws Exception {

		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

		URI fullURI = prepareOrderURI(customerId);

		HttpGet request = new HttpGet(fullURI);

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		return EntityUtils.toString(entity);

	}

	private URI prepareOrderURI(String customerId) {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(customerId)) {
			if (filter.length() > 0) {
				filter.append(IntegrationConstants.AND);
			}
			filter.append("customer_id eq " + customerId);

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

	}

	private URI prepareContactURI(String email) {

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

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}
}
