package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.p21.service.P21OrderLineService;

@Service
public class P21OrderLineServiceImpl implements P21OrderLineService {

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

	@Override
	public String getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters) {
		String orderData = "";
		try {
			orderData = getOrderData(orderSearchParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderData;
	}

	private String getOrderData(OrderSearchParameters orderSearchParameters) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fulluri = prepareOrderURI(orderSearchParameters);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		RequestEntity<Void> requestMapping = new RequestEntity<>(headers, HttpMethod.GET, fulluri);
		ResponseEntity<String> response = restTemplate.exchange(requestMapping, String.class);
		System.out.println(response.getBody());
		return response.getBody();
	}

	private URI prepareOrderURI(OrderSearchParameters orderSearchParameters) throws URISyntaxException {

		StringBuilder filter = new StringBuilder();

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			filter.append("original_invoice_no eq '" + orderSearchParameters.getInvoiceNo() + "'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getPoNo())) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("order_no eq '" + orderSearchParameters.getPoNo() + "'");
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("customer_id eq " + orderSearchParameters.getCustomerId());
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			if (filter.length() > 0) {
				filter.append(" and ");
			}
			filter.append("ship2_zip eq '" + orderSearchParameters.getZipcode() + "'");
		}

		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter="+ encodedFilter;
			URI uri = new URI(DATA_API_BASE_URL + DATA_API_ORDER_LINE);
			URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
			return fullURI;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}
}
