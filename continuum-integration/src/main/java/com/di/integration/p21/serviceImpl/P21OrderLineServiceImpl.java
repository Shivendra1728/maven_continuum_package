package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import com.continuum.repos.entity.ClientConfig;
import com.continuum.repos.entity.Customer;
import com.continuum.repos.repositories.CustomerRepository;
import com.continuum.repos.repositories.StoreRepository;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.dto.StoreDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21OrderLineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class P21OrderLineServiceImpl implements P21OrderLineService {

	private static final Logger logger = LoggerFactory.getLogger(P21OrderLineServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_LINE)
	String DATA_API_ORDER_LINE;

	@Value(IntegrationConstants.ERP_ORDER_LINE_SELECT_FIELDS)
	String ORDER_LINE_SELECT_FIELDS;

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

	LocalDate localDate;

	@Override
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters, int totalItem)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception {
		
		List<OrderItemDTO> orderItemDTOList=p21orderLineItemMapper
		.convertP21OrderLineObjectToOrderLineDTO(getOrderLineData(orderSearchParameters, totalItem),orderSearchParameters);
		return orderItemDTOList;
	}

	private String getOrderLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fulluri = prepareOrderLineURI(orderSearchParameters, totalItem);
		logger.info("getOrderLineData URI: " + fulluri);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		RequestEntity<Void> requestMapping = new RequestEntity<>(headers, HttpMethod.GET, fulluri);
		ResponseEntity<String> response = restTemplate.exchange(requestMapping, String.class);
		return response.getBody();
	}

	private URI prepareOrderLineURI(OrderSearchParameters orderSearchParameters, int totalItem)
			throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			// filter.append("original_invoice_no eq '" +
			// orderSearchParameters.getInvoiceNo() + "'");

			filter.append(IntegrationConstants.ORIGINAL_INVOICE_NO).append(" ")
					.append(IntegrationConstants.CONDITION_EQ).append(" '").append(orderSearchParameters.getInvoiceNo())
					.append("'");
		}

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

		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter=" + encodedFilter;
			if (totalItem == 1) {
				query = query + "&$top=1";
			}
			URI uri = new URI(DATA_API_BASE_URL + DATA_API_ORDER_LINE);
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

}
