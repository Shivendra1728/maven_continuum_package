package com.continuum.multitenant.controller;

import java.io.Serializable;
import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.continuum.multitenant.security.UserTenantInformation;

/**
 * @author RK
 */
@RestController
@RequestMapping("/user")
public class LogoutController implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutController.class);

	@Autowired
	ApplicationContext applicationContext;

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<?> logoutFromApp(Principal principal) {
		LOGGER.info("AuthenticationController::logoutFromApp() method call..");
		UserTenantInformation userCharityInfo = applicationContext.getBean(UserTenantInformation.class);
		Map<String, String> map = userCharityInfo.getMap();
		map.remove(principal.getName());
		userCharityInfo.setMap(map);
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
