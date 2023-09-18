package com.continuum.serviceImpl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.di.commons.dto.ContactDTO;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.mapper.CustomerMapper;
import com.di.commons.p21.mapper.P21ContactMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.serviceImpl.P21OrderServiceImpl;
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;
import com.di.commons.dto.OrderDTO;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CustomerRepository repo;

	@Autowired
	CustomerMapper customerMapper;

	@Autowired
	P21ContactMapper p21ContactMapper;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	public CustomerDTO findbyCustomerId(String customerId) {
		Customer customer = repo.findByCustomerId(customerId);
		return customerMapper.cusotmerTocusotmerDTO(customer);
	}

	public CustomerDTO createCustomer(CustomerDTO custDTO) throws MessagingException {

		ContactDTO contactDTO = null;
		try {
			contactDTO = p21ContactMapper.convertP21ContactObjectToContactDTO(getContactData(custDTO.getEmail()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (contactDTO.getCustId() != null) {
			if (repo.existsByEmail(custDTO.getEmail())) {
				throw new MessagingException("Email already exists.");
			}

			Customer customer = customerMapper.cusotmerDTOTocusotmer(custDTO);
			customer.setCustomerId(contactDTO.getCustId());
			customer.setPhone(contactDTO.getContactPhoneNo());
			customer.setDisplayName(contactDTO.getContactName());
			Customer savedCustomer = repo.save(customer);
			return customerMapper.cusotmerTocusotmerDTO(savedCustomer);

		} else {
			throw new MessagingException("You are not a customer of Us!");

		}

	}

	private String getContactData(String email) throws Exception {

		// RestTemplate restTemplate = new RestTemplate();

		// Add the Bearer token to the request headers

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fullURI = prepareContactURI(email);
		logger.info("getContactData URI:" + fullURI);
		// Set the Accept header to receive JSON response
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$
		// Create the request entity with headers
		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, fullURI);
		// Make the API call
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		return response.getBody();
		// Process the API response

		/*
		 * 
		 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
		 * 
		 * response.getBody(); System.out.println("API response: " + responseBody); }
		 * 
		 * else { System.err.println("API call failed with status code: " +
		 * 
		 * response.getStatusCodeValue()); }
		 * 
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


}
