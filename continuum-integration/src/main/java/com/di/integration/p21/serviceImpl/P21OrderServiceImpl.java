package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

@Service
public class P21OrderServiceImpl implements P21OrderService {

	private static final Logger logger = LoggerFactory.getLogger(P21OrderServiceImpl.class);
	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_VIEW)
	String DATA_API_ORDER_VIEW;

	// @Value("${erp.token}") //property also commented
	// String TOKEN;

	@Value(IntegrationConstants.ERP_ORDER_SELECT_FIELDS)
	String ORDER_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

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

	@Override
	public List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) throws Exception {
		List<OrderDTO> orderDTOList = new ArrayList<>();
		List<OrderItemDTO> orderItemDTOList  = new ArrayList<>();
		if (!isNotNullAndNotEmpty(orderSearchParameters.getOrderNo()) && isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			int totalItem = 1;
			List<OrderItemDTO> invoiceItemDTOList = p21InvoiceMapper
					.mapP21InvoiceResponseToData(p21InvoiceServiceImpl.getInvoiceLineData(orderSearchParameters, totalItem)); // Invoice header
			//int totalItem = 1; // fetching one item to get invoice no from item
			// orderItemDTOList = p21OrderLineServiceImpl
				//	.getordersLineBySearchcriteria(orderSearchParameters, totalItem,orderSearchParameters.getInvoiceNo());
			if (invoiceItemDTOList.size() > 0) {
				orderSearchParameters.setOrderNo(invoiceItemDTOList.get(0).getOrderNo());
				orderDTOList = getAllOrdersBySearch(orderSearchParameters,orderItemDTOList);
			}

		} else {
			orderDTOList = getAllOrdersBySearch(orderSearchParameters,orderItemDTOList);
		}
		
		
		return orderDTOList;
	}

	private List<OrderDTO> getAllOrdersBySearch(OrderSearchParameters orderSearchParameters,List<OrderItemDTO> orderItemDTOList)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception {
		List<OrderDTO> orderDTOList = new ArrayList<>();
		orderDTOList = p21OrderMapper.convertP21OrderObjectToOrderDTO(getOrderData(orderSearchParameters));
	
		if(orderDTOList.size()>0) {
			 clientConfig = clientConfigRepository.findByErpCompanyId(orderDTOList.get(0).getCompanyId());
		}
			for (OrderDTO orderDTO : orderDTOList) {
			int totalItem = -1; // fetch all items in case of -1
			OrderSearchParameters orderSearchParams = new OrderSearchParameters();
			orderSearchParams.setOrderNo(orderDTO.getOrderNo());
			if(orderItemDTOList.size()==0) {
				orderItemDTOList = p21OrderLineServiceImpl
						.getordersLineBySearchcriteria(orderSearchParams, totalItem,orderSearchParameters.getInvoiceNo());
			}
			
			if(isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())){
				orderItemDTOList=	orderItemDTOList.stream().filter(item->item.getInvoiceNo()!=null).collect(Collectors.toList());
			}
			orderDTO.setOrderItems(orderItemDTOList);
			orderDTO.setContactDTO(p21ContactMapper.convertP21ContactObjectToContactDTO(getContactData(orderDTO.getContactEmailId())));
		
			if (clientConfig != null && clientConfig.getReturnPolicyPeriod()!=null) {
					localDate = LocalDate.now().minusDays(clientConfig.getReturnPolicyPeriod());
					logger.info("Return policy period: " + clientConfig.getReturnPolicyPeriod());

			if (localDate != null) {
				
				for (OrderItemDTO orderItemDTO : orderItemDTOList) {
					if(orderItemDTO.getInvoiceDate()!=null) {
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

	private String getOrderData(OrderSearchParameters orderSearchParameters) throws Exception {
		
		CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
		
		URI fullURI = prepareOrderURI(orderSearchParameters);
		
		 HttpGet request = new HttpGet(fullURI);

		 request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.getToken());

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

		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.getToken());

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

		try {

			String filter = "email_address eq '" + email + "'";

			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter;

			URI uri = new URI(DATA_API_BASE_URL + IntegrationConstants.ENDPOINT_VIEW_CONTACTS);
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

			URI uri = new URI(DATA_API_BASE_URL + DATA_API_ORDER_VIEW);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			logger.info("Filtering orders with order_date greater than or equal to: {}", localDate);
			logger.info("Current date: {}", LocalDate.now());

			return fullURI;
		} catch (Exception e) {

			logger.error("An error occurred while preparing the order URI: {}", e.getMessage());

		}
		return null;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

}