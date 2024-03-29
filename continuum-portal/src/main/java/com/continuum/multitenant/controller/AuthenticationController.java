package com.continuum.multitenant.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.ApplicationScope;

import com.continuum.multitenant.constant.UserStatus;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.service.MasterTenantService;
import com.continuum.multitenant.security.UserTenantInformation;
import com.continuum.multitenant.util.JwtTokenUtil;
import com.continuum.serviceImpl.ReturnOrderServiceImpl;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.RolesRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.AuthResponse;
import com.di.commons.dto.UserLoginDTO;
import com.di.commons.helper.DBContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author RK
 */

@Slf4j
@RestController
public class AuthenticationController implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

	private Map<String, String> mapValue = new HashMap<>();
	private Map<String, String> userDbMap = new HashMap<>();

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	MasterTenantService masterTenantService;
	@Autowired
	UserRepository userRepository;

	@Autowired
	RolesRepository rolesRepository;

	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	ReturnOrderServiceImpl returnOrderServiceImpl;


//    @Autowired
//    private TenantInfoHolderContext tenantInfoHolderContext;

//	@Autowired
//	private TenantInfoHolder tenantInfoHolder;

	public static String domain = "";

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> userLogin(@RequestBody @NotNull UserLoginDTO userLoginDTO, HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {
		LOGGER.info("userLogin() method call...");
		if (null == userLoginDTO.getUserName() || userLoginDTO.getUserName().isEmpty()) {
			return new ResponseEntity<>("User name is required", HttpStatus.BAD_REQUEST);
		}
		// set database parameter

//		String tenentId = request.getHeader("host").split("\\.")[0];
		String tenentId = request.getHeader("tenant");
		MasterTenant masterTenant = masterTenantService.findByDbName(tenentId);
		if (null == masterTenant || masterTenant.getStatus().toUpperCase().equals(UserStatus.INACTIVE)) {
			throw new RuntimeException("Please contact service provider.");
		}

		domain = masterTenant.getSubdomain();

//		IntegrationConstants.DomainUsername = masterTenant.getDomainUsername();
//		
//		IntegrationConstants.DomainPassword = masterTenant.getDomainPassword();

//		httpSession.setAttribute("subdomain", masterTenant.getSubdomain());
//		httpSession.setAttribute("domainUsername", masterTenant.getDomainUsername());
//		httpSession.setAttribute("domainPassword", masterTenant.getDomainPassword());	

//		TenantInfoHolder tenantInfoHolder = new TenantInfoHolder();
//		tenantInfoHolder.setDomain(masterTenant.getSubdomain());
//		tenantInfoHolder.setDomainUsername(masterTenant.getDomainUsername());
//		tenantInfoHolder.setDomainPassword(masterTenant.getDomainPassword());
//
//		// Set in ThreadLocal
//		TenantInfoHolderContext.setCurrentTenantInfo(tenantInfoHolder);
//
//		log.info("Subdomain : - " + TenantInfoHolderContext.getCurrentTenantInfo().getDomain());
//		// TenantInfoHolderContext.setCurrentTenantInfo(tenantInfoHolder);

//		log.info(tenantInfoHolder.getDomainUsername());
//
//		tenantInfoProvider.updateTenantInfo(tenantInfoHolder.getDomain(), tenantInfoHolder.getDomainUsername(),
//				tenantInfoHolder.getDomainPassword());

		// Entry Client Wise value dbName store into bean.
		loadCurrentDatabaseInstance(masterTenant.getDbName(), userLoginDTO.getUserName());
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(), userLoginDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		final String token = jwtTokenUtil.generateToken(userDetails.getUsername(), tenentId);
		User user = userRepository.findByUserName(userDetails.getUsername());
		String rmaQualifier = returnOrderServiceImpl.getRmaaQualifier();

		if (!user.getStatus()) {
			throw new RuntimeException("User account is inactive.");
		}

		String name = user.getFirstName() + " " + user.getLastName();
		long userId = user.getId();

		if (user.getCustomer() == null) {

			Customer existingCustomer = customerRepository.findByCustomerId("");

			if (existingCustomer != null) {

				user.setCustomer(existingCustomer);

			} else {
				Customer newCustomer = new Customer();

				newCustomer.setCustomerId("");

				customerRepository.save(newCustomer);

				user.setCustomer(newCustomer);

			}
			userRepository.save(user);
		}
		// Set<Role> role = u.getRoles();

		// final String token =
		// jwtTokenUtil.generateToken(userDetails.getUsername(),String.valueOf(userLoginDTO.getTenantOrClientId()));
		// Map the value into applicationScope bean
		setMetaDataAfterLogin();
		return ResponseEntity.ok(new AuthResponse(userDetails.getUsername(), name, token, user.getRole(), userId,
				user.getCustomer().getCustomerId(), jwtTokenUtil.getExpirationDateFromToken(token), rmaQualifier));
	}

	private void loadCurrentDatabaseInstance(String databaseName, String userName) {
		DBContextHolder.setCurrentDb(databaseName);
		mapValue.put(userName, databaseName);
	}

	@Bean(name = "userTenantInfo")
	@ApplicationScope
	public UserTenantInformation setMetaDataAfterLogin() {
		UserTenantInformation tenantInformation = new UserTenantInformation();
		if (mapValue.size() > 0) {
			for (String key : mapValue.keySet()) {
				if (null == userDbMap.get(key)) {
					// Here Assign putAll due to all time one come.
					userDbMap.putAll(mapValue);
				} else {
					userDbMap.put(key, mapValue.get(key));
				}
			}
			mapValue = new HashMap<>();
		}
		tenantInformation.setMap(userDbMap);
		return tenantInformation;
	}
}
