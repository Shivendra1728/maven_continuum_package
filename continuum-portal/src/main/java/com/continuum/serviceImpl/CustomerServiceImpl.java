package com.continuum.serviceImpl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.service.CustomerService;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.Role;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.RolesRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.OrderDTO;
import com.di.commons.mapper.CustomerMapper;
import com.di.commons.p21.mapper.P21OrderMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;

@Service
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CustomerMapper customerMapper;

	@Autowired
	UserRepository userRepository;

	@Autowired
	P21OrderMapper p21OrderMapper;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	RolesRepository rolesRepository;

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_VIEW)
	String DATA_API_ORDER_VIEW;

	LocalDate localDate;

	public CustomerDTO findbyCustomerId(String customerId) {
		Customer customer = customerRepository.findByCustomerId(customerId);
		return customerMapper.cusotmerTocusotmerDTO(customer);
	}

	public String createCustomerInDB(CustomerDTO customerDTO) {

		OrderDTO orderDTO = null;

		try {
			orderDTO = p21OrderMapper.convertP21OrderObjectToOrderDTOForCustomer(getOrderData(customerDTO.getEmail()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orderDTO != null) {
			if (orderDTO.getCustomer() == null) {
				return "You are not a customer of us!";
			}
			if (userRepository.existsByEmail(customerDTO.getEmail())) {
				return "Email already exists.";
			}

			Customer customer = new Customer();
			customer.setCustomerId(orderDTO.getCustomer().getCustomerId());

			customerRepository.save(customer);

			User user = new User();
			Role role = rolesRepository.findById(4L).orElse(null);
			user.setFirstName(customerDTO.getFirstName());
			user.setLastName(customerDTO.getLastname());
			String hashedPassword = BCrypt.hashpw(customerDTO.getPassword(), BCrypt.gensalt());
			user.setPassword(hashedPassword);
			user.setUserName(customerDTO.getEmail());
			user.setEmail(customerDTO.getEmail());
			user.setStatus(true);
			user.setCustomer(customer);
			if (role != null) {
				user.setRoles(role);
			}
			user.setFullName("None");
			userRepository.save(user);
			return "Customer Signed Up SuccessFully";

		} else {

			return "You are not a customer of Us!";

		}

	}

	private String getOrderData(String email) throws Exception {
		// RestTemplate restTemplate = new RestTemplate();
		// Add the Bearer token to the request headers
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		URI fullURI = prepareOrderURI(email);
		// Set the Accept header to receive JSON response
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$

		// Create the request entity with headers
		logger.info("Order Search URI:" + fullURI);
		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, fullURI);

		// Make the API call
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		return response.getBody();
		// Process the API response
		/*
		 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
		 * response.getBody(); System.out.println("API response: " + responseBody); }
		 * else { System.err.println("API call failed with status code: " +
		 * response.getStatusCodeValue()); }
		 */
	}

	private URI prepareOrderURI(String email) {

		try {
			String filter = "contact_email_address eq '" + email + "'";
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter
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

	@Override
	public CustomerDTO createCustomer(CustomerDTO custDTO) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
