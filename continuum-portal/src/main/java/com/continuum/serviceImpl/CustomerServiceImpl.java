package com.continuum.serviceImpl;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.constants.PortalConstants;
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

	@Autowired
	EmailSender emailSender;

	@Lazy
	@Autowired
	ReturnOrderServiceImpl returnOrderServiceImpl;

	LocalDate localDate;

	private final EmailTemplateRenderer emailTemplateRenderer;

	@Autowired
	public CustomerServiceImpl(EmailTemplateRenderer emailTemplateRenderer) {
		this.emailTemplateRenderer = emailTemplateRenderer;
	}

	// EmailTemplateRenderer emailTemplateRenderer = new EmailTemplateRenderer();

	public CustomerDTO findbyCustomerId(String customerId) {
		Customer customer = customerRepository.findByCustomerId(customerId);
		return customerMapper.cusotmerTocusotmerDTO(customer);
	}

	public Map<String, Object> createCustomerInDB(CustomerDTO customerDTO, HttpServletRequest request) {
		Map<String, Object> jsonResponse = new HashMap<>();
		String uuid = UUID.randomUUID().toString();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		Date expirationTime = calendar.getTime();

		OrderDTO orderDTO = null;

		try {
			orderDTO = p21OrderMapper.convertP21OrderObjectToOrderDTOForCustomer(getOrderData(customerDTO.getEmail()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orderDTO != null) {
			if (orderDTO.getCustomer() == null) {
				jsonResponse.put("status", false);
				jsonResponse.put("message", "You are not a customer of us!");
				return jsonResponse;
			}
			if (userRepository.existsByEmail(customerDTO.getEmail())) {
				jsonResponse.put("status", false);
				jsonResponse.put("message", "Email already exists.");
				return jsonResponse;
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
			user.setStatus(false);
			user.setActivationUuid(uuid);
			user.setActivationResetTokenExpiration(expirationTime);
			user.setCustomer(customer);
			if (role != null) {
				user.setRole(role);
			}
			user.setFullName("None");
			userRepository.save(user);

//			String recipient = PortalConstants.EMAIL_RECIPIENT;
			String tenentId = httpServletRequest.getHeader("tenant");
			MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
			String recipient = "";

			if (masterTenant.getIsProd()) {
				recipient = customerDTO.getEmail();
			} else {
//				recipient = PortalConstants.EMAIL_RECIPIENT;
				recipient = masterTenant.getDefaultEmail();

			}

			String subject = "Activate Your Account";

//			emailSender.sendEmail(recipient, subject, body, returnOrderDTO, customerDTO);
			HashMap<String, String> map = new HashMap<>();
			String fullUrl = request.getRequestURL().toString();
			try {
				URL url = new URL(fullUrl);
				String host = url.getHost();
				String scheme = request.getScheme();
//				String link = scheme + "://" + host + "/userlogin?token=" + uuid;
				String link = "";
				if (host.contains("uat")) {
					link = scheme + "://" + request.getHeader("tenant") + ".uat.gocontinuum.ai" + "/userlogin?token="
							+ uuid;
				} else if (host.contains("dev")) {
					link = scheme + "://" + request.getHeader("tenant") + ".dev.gocontinuum.ai" + "/userlogin?token="
							+ uuid;

				} else {
					link = scheme + "://" + request.getHeader("tenant") + ".gocontinuum.ai" + "/userlogin?token="
							+ uuid;
				}
				System.err.println("Link : " + link);

				map.put("cust_name", orderDTO.getCustomer().getDisplayName());
				map.put("cust_email", customerDTO.getEmail());
				map.put("RESET_LINK", link);
				map.put("user_id", user.getId().toString());
				map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
				map.put("CLIENT_PHONE",
						String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));

				String template = emailTemplateRenderer.getACTIVATE_ACCOUNT();

				emailSender.sendEmail(recipient, template, subject, map);
			} catch (Exception e) {
				e.printStackTrace();
			}

			jsonResponse.put("status", true);
			jsonResponse.put("message", "Email sent successfully!");
			return jsonResponse;

		} else {
			jsonResponse.put("status", false);
			jsonResponse.put("message", "You are not a customer of us!");
			return jsonResponse;
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

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));
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

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId = httpServletRequest.getHeader("tenant");

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		try {
			String filter = "contact_email_address eq '" + email + "'";
			String encodedFilter = URLEncoder.encode(filter.toString(), StandardCharsets.UTF_8.toString());
			String query = "$format=" + ORDER_FORMAT + "&$select=" + "&$filter=" + encodedFilter
					+ "&$top=1&$orderby=order_date";

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

	@Override
	public CustomerDTO createCustomer(CustomerDTO custDTO) throws Exception {
		
		return null;
	}

}
