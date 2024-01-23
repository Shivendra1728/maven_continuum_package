package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
	

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	LocalDate localDate;

	@Override
	public List<OrderItemDTO> getordersLineBySearchcriteria(OrderSearchParameters orderSearchParameters, int totalItem,String invoiceNo)
			throws JsonMappingException, JsonProcessingException, ParseException, Exception {
		
		List<OrderItemDTO> orderItemDTOList=p21orderLineItemMapper
		.convertP21OrderLineObjectToOrderLineDTO(getOrderLineData(orderSearchParameters, totalItem),orderSearchParameters,invoiceNo);
		return orderItemDTOList;
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


		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		
		
		try {
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=&$filter=" + encodedFilter;
			if (totalItem == 1) {
				query = query + "&$top=1";
			}
			URI uri = new URI(masterTenant.getSubdomain()+DATA_API_BASE_URL + DATA_API_ORDER_LINE);
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
