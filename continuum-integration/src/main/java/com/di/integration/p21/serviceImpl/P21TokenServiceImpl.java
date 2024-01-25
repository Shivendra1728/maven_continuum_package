package com.di.integration.p21.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Properties;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21TokenSerivce;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;

@Service
public class P21TokenServiceImpl implements P21TokenSerivce {

	private static final Logger logger = LoggerFactory.getLogger(P21TokenServiceImpl.class);
//
//	@Autowired
//	TenantInfoHolderContext tenantInfoHolderContext;

	@Value(IntegrationConstants.ERP_TOKEN_ENDPOINT)
	private String TOKEN_ENDPOINT;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public String getToken(MasterTenant masterTenantObject) throws Exception {

		String accessToken = getAccessTokenFromCookie();
		logger.info("getAccessTokenFromCookie::" + accessToken);
		if (accessToken == null) {
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			

			MasterTenant masterTenant;

			if (masterTenantObject == null) {
//			    String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
				String tenantId = httpServletRequest.getHeader("tenant");
			    masterTenant = masterTenantRepository.findByDbName(tenantId);
			} else {
			    masterTenant = masterTenantObject;
			}
		
			URIBuilder uriBuilder = new URIBuilder(masterTenant.getSubdomain() + TOKEN_ENDPOINT);
			logger.info("URI : "+uriBuilder.toString());
			uriBuilder.addParameter("username", masterTenant.getDomainUsername());
			uriBuilder.addParameter("password", masterTenant.getDomainPassword());
			HttpPost request = new HttpPost(uriBuilder.build());
			logger.info("Subdomain: " + masterTenant.getSubdomain());
			logger.info("Username: " + masterTenant.getDomainUsername());
			logger.info("Password: " + masterTenant.getDomainPassword());

			// Set request headers
			request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			// Execute the request
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity);
		}
		return accessToken;

	}

	private String getAccessTokenFromCookie() {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();

			Cookie[] cookies = request.getCookies();

			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("accessToken")) {
						String accessToken = cookie.getValue();

						// Use the token as needed
						return accessToken;
					}
				}
			}
			return null;
		} catch (Exception e) {
			logger.error("token not found in cookie");
			e.printStackTrace();
		}
		return null;

	}
	
	public String findToken(MasterTenant masterTenantObject) throws Exception {
	    if (masterTenantObject == null) {
//	        String tenantId = httpServletRequest.getHeader("host").split("\\.")[0];
	    	String tenantId = httpServletRequest.getHeader("tenant");
	        masterTenantObject = masterTenantRepository.findByDbName(tenantId);
	    }

	    CachingProvider cachingProvider = Caching.getCachingProvider();

	    // Explicitly provide key and value types
	    CacheManager cacheManager = cachingProvider.getCacheManager(null, null, new Properties());

	    // Check if the cache already exists
	    Cache<String, String> cache = cacheManager.getCache("JDKCodeNames", String.class, String.class);
	    if (cache == null) {
	        // If the cache does not exist, create a new one with explicit key and value types
	        MutableConfiguration<String, String> config = new MutableConfiguration<>();
	        config.setTypes(String.class, String.class); // Set explicit types
	        cache = cacheManager.createCache("JDKCodeNames", config);
	    }

	    String cacheKey = String.valueOf(masterTenantObject.getDbName()); // Convert the key to String
	   	if (cache.containsKey(cacheKey) && !isTokenExpired(cache.get(cacheKey))) {
	        logger.info("Cache Token: " + cache.get(cacheKey));
	        return cache.get(cacheKey);
	    } else {
	        String token = getToken(masterTenantObject);
	        logger.info("New Token: " + token);
	        cache.put(cacheKey, token);
	        return token;
	    }
	}

	public boolean isTokenExpired(String token) {
		try {
			String body = token.split("\\.")[1];
			String payload = new String(Base64.getDecoder().decode(body));
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(payload);
			long expValue = jsonNode.get("exp").asLong();
			LocalDateTime expirationTime = convertTimestampToDateTime(expValue);
			LocalDateTime currentTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
			LocalDateTime formatedTime = LocalDateTime.parse(formatter.format(currentTime));
			logger.info("Is Token Expired : "+formatedTime.isAfter(expirationTime));
			return formatedTime.isAfter(expirationTime);
		} catch (ExpiredJwtException e) {
			// The token has expired
			return true;
		} catch (Exception e) {
			// Other exceptions, such as parsing errors
			return false;
		}
	}
	
	private static LocalDateTime convertTimestampToDateTime(long timestamp) {
		Instant instant = Instant.ofEpochMilli(timestamp * 1000); // Convert seconds to milliseconds
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

	
}
