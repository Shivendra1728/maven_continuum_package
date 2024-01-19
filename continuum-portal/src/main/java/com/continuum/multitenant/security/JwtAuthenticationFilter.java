package com.continuum.multitenant.security;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.continuum.multitenant.constant.JWTConstants;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.service.MasterTenantService;
import com.continuum.multitenant.util.JwtTokenUtil;
import com.di.commons.helper.DBContextHolder;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

/**
 * @author RK
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	MasterTenantService masterTenantService;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		String header = httpServletRequest.getHeader(JWTConstants.HEADER_STRING);
		String username = null;
		String audience = null; // tenantOrClientId
		String authToken = null;
		String URL = httpServletRequest.getRequestURI();
		if (header != null && header.startsWith(JWTConstants.TOKEN_PREFIX)) {
			authToken = header.replace(JWTConstants.TOKEN_PREFIX, "");
			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);
				audience = jwtTokenUtil.getAudienceFromToken(authToken);
				String host = httpServletRequest.getHeader("host").split("\\.")[0];
				MasterTenant masterTenant = masterTenantService.findByDbName((audience));
				if (null == masterTenant) {
					logger.error("An error during getting tenant name");
					throw new BadCredentialsException("Invalid tenant and user.");
				}
				DBContextHolder.setCurrentDb(host);
			} catch (IllegalArgumentException ex) {
				logger.error("An error during getting username from token", ex);
			} catch (ExpiredJwtException ex) {
				logger.warn("The token is expired and not valid anymore", ex);
			} catch (SignatureException ex) {
				logger.error("Authentication Failed. Username or Password not valid.", ex);
			}
		} else if (httpServletRequest.getRequestURI().contains("/forgetPassword")
				|| httpServletRequest.getRequestURI().contains("/signupCust")
				|| httpServletRequest.getRequestURI().contains("/updatePassword")
				|| httpServletRequest.getRequestURI().contains("/api/upload-csv")
				|| httpServletRequest.getRequestURI().contains("/activateAccount")) {
			String host = httpServletRequest.getHeader("host").split("\\.")[0];
			// MasterTenant masterTenant = masterTenantService.findByDbName((audience));
			// if(null == masterTenant){
			// logger.error("An error during getting tenant name");
			// throw new BadCredentialsException("Invalid tenant and user.");
			// }
			DBContextHolder.setCurrentDb(host);

		} else {
			logger.warn("Couldn't find bearer string, will ignore the header");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				logger.info("authenticated user " + username + ", setting security context");
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
