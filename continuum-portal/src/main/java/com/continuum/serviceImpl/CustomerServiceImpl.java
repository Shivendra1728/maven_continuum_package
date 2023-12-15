package com.continuum.serviceImpl;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
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
	
	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

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
			customer.setEmail(orderDTO.getCustomer().getEmail());
			customer.setDisplayName(orderDTO.getCustomer().getDisplayName());
			customer.setPhone(orderDTO.getCustomer().getPhone());

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
				user.setRole(role);
			}
			user.setFullName("None");
			userRepository.save(user);
			return "Customer Signed Up SuccessFully";

		} else {

			return "You are not a customer of us!";

		}

	}

	private String getOrderData(String email) throws Exception {
		// RestTemplate restTemplate = new RestTemplate();
		// Add the Bearer token to the request headers
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		URI fullURI = prepareOrderURI(email);
		logger.info("Order Search URI:" + fullURI);
		HttpGet request = new HttpGet(fullURI);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.getToken(null));
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();
		String responseBody = EntityUtils.toString(entity);
		logger.info("response :" + response);
		return responseBody;
		// Process the API response
		/*
		 * if (response.getStatusCode().is2xxSuccessful()) { responseBody =
		 * response.getBody(); System.out.println("API response: " + responseBody); }
		 * else { System.err.println("API call failed with status code: " +
		 * response.getStatusCodeValue()); }
		 */
	}

	private URI prepareOrderURI(String email) {
		
		

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		try {
			String filter = "contact_email_address eq '" + email + "'";
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter
					+ "&$top=1&$orderby=order_date";

			URI uri = new URI(masterTenant.getSubdomain()+DATA_API_BASE_URL + DATA_API_ORDER_VIEW);
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
