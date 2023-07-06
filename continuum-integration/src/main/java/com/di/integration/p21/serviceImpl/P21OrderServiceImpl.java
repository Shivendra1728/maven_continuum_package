package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.p21.mapper.P21ContactMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.p21.service.P21OrderService;

@Service
public class P21OrderServiceImpl implements P21OrderService {

	private static final Logger logger = LoggerFactory.getLogger(P21OrderServiceImpl.class);
	@Autowired
	RestTemplate restTemplate;

	@Value("${erp.data_api_base_url}")
	String DATA_API_BASE_URL;

	@Value("${erp.data_api_order_view}")
	String DATA_API_ORDER_VIEW;

	//@Value("${erp.token}") //property also commented
	//String TOKEN;

	@Value("${erp.order_select_fields}")
	String ORDER_SELECT_FIELDS;

	@Value("${erp.order_format}")
	String ORDER_FORMAT;
	
	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	
	
	@Autowired
	P21OrderMapper p21OrderMapper;
	
	@Autowired
	P21OrderLineServiceImpl p21OrderLineServiceImpl;
	
	@Autowired
	P21ContactMapper p21ContactMapper;

	@Override
	public List<OrderDTO> getOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) throws Exception {
		
			List<OrderDTO>  orderDTOList=p21OrderMapper.convertP21OrderObjectToOrderDTO(getOrderData(orderSearchParameters));
			for (OrderDTO orderDTO : orderDTOList) {
				OrderSearchParameters orderSearchParams= new OrderSearchParameters();
				orderSearchParams.setOrderNo(orderDTO.getOrderNo());
				List<OrderItemDTO> orderItemDTOList= p21OrderLineServiceImpl.getordersLineBySearchcriteria(orderSearchParams);
				orderDTO.setOrderItems(orderItemDTOList);
				orderDTO.setContactDTO(p21ContactMapper.convertP21ContactObjectToContactDTO(getContactData(orderDTO.getContactEmailId())));
			} 
			
			
			return orderDTOList;
	}
	private String getOrderData(OrderSearchParameters orderSearchParameters) throws Exception{
			// RestTemplate restTemplate = new RestTemplate();
			// Add the Bearer token to the request headers
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(p21TokenServiceImpl.getToken());
			URI fullURI = prepareOrderURI(orderSearchParameters);
			// Set the Accept header to receive JSON response
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$

			// Create the request entity with headers
			logger.info("Order Search URI:"+fullURI);
			RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, fullURI);

			// Make the API call
			ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
			return  response.getBody();
			// Process the API response
			/*
			 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
			 * response.getBody(); System.out.println("API response: " + responseBody); }
			 * else { System.err.println("API call failed with status code: " +
			 * response.getStatusCodeValue()); }
			 */
	}
	
	private String getContactData(String email) throws Exception{
		// RestTemplate restTemplate = new RestTemplate();
		// Add the Bearer token to the request headers
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fullURI = prepareContactURI(email);
		// Set the Accept header to receive JSON response
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$

		// Create the request entity with headers
		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, fullURI);

		// Make the API call
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		return  response.getBody();
		// Process the API response
		/*
		 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
		 * response.getBody(); System.out.println("API response: " + responseBody); }
		 * else { System.err.println("API call failed with status code: " +
		 * response.getStatusCodeValue()); }
		 */
}
	
	private URI prepareContactURI(String email) {
		
		//p21_view_contacts?$select=&$filter=email_address eq 'SOUSADA.SALINTHONE@AZZUR.COM'&$format=json
				
				try {

					String filter = "email_address eq '"+email+"'";
					
					String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
					String query = "$format=" + ORDER_FORMAT + "&$select=" +"&$filter=" + encodedFilter;

					URI uri = new URI(DATA_API_BASE_URL + "p21_view_contacts");
					URI fullURI = uri.resolve(uri.getRawPath() + "?" + query);
					return fullURI;
				} catch (Exception e) {
					System.out.println(e.getMessage());

				}
				return null;
	}
	
	

	private URI prepareOrderURI(OrderSearchParameters orderSearchParameters) {
		
		StringBuilder filter =new StringBuilder();
		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			filter.append("ship2_zip eq '"+orderSearchParameters.getZipcode()+"'");
		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			 if (filter.length() > 0) {
			        filter.append(" and ");
			    }
			filter.append("customer_id eq "+orderSearchParameters.getCustomerId());
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getPoNo())) {
			 if (filter.length() > 0) {
			        filter.append(" and ");
			    }
			filter.append("po_number eq '"+orderSearchParameters.getPoNo()+"'");
			 
		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			 if (filter.length() > 0) {
			        filter.append(" and ");
			    }
			filter.append("original_invoice_no eq '"+orderSearchParameters.getInvoiceNo()+"'");
			 
		}
		try {

			//String filter = "customer_id eq 157108 and ship2_zip eq '35811'";
			
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + ORDER_SELECT_FIELDS + "&$filter=" + encodedFilter;

			URI uri = new URI(DATA_API_BASE_URL + DATA_API_ORDER_VIEW);
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
