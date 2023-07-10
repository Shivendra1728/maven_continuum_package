package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collections;
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

import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21OrderLineItemMapper;
import com.di.integration.p21.service.P21OrderLineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class P21OrderLineServiceImpl implements P21OrderLineService {
	
	private static final Logger logger = LoggerFactory.getLogger(P21OrderLineServiceImpl.class);

	@Autowired
	RestTemplate restTemplate;

	@Value("${erp.data_api_base_url}")
	String DATA_API_BASE_URL;

	@Value("${erp.data_api_order_line}")
	String DATA_API_ORDER_LINE;

	@Value("${erp.order_line_select_fields}")
	String ORDER_LINE_SELECT_FIELDS;

	@Value("${erp.order_format}")
	String ORDER_FORMAT;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	
	@Autowired
	P21OrderLineItemMapper p21orderLineItemMapper;

	@Override
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters,int totalItem) throws JsonMappingException, JsonProcessingException, ParseException, Exception {
			return p21orderLineItemMapper.convertP21OrderLineObjectToOrderLineDTO(getOrderLineData(orderSearchParameters,totalItem));
			
	}

	private String getOrderLineData(OrderSearchParameters orderSearchParameters, int totalItem) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fulluri = prepareOrderLineURI(orderSearchParameters,totalItem);
		logger.info("getOrderLineData URI: "+fulluri);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		RequestEntity<Void> requestMapping = new RequestEntity<>(headers, HttpMethod.GET, fulluri);
		ResponseEntity<String> response = restTemplate.exchange(requestMapping, String.class);
		return response.getBody();
	}

	private URI prepareOrderLineURI(OrderSearchParameters orderSearchParameters,int totalItem) throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			filter.append("original_invoice_no eq '" + orderSearchParameters.getInvoiceNo() + "'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getOrderNo())) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("order_no eq '" + orderSearchParameters.getOrderNo() + "'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("customer_id eq " + orderSearchParameters.getCustomerId());
		}


		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter=" + encodedFilter;
			if(totalItem==1) {
				query=query+"&$top=1";
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
