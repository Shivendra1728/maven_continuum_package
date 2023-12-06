package com.di.integration.p21.serviceImpl;

import java.io.StringReader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21TokenSerivce;

@Service
public class P21TokenServiceImpl implements P21TokenSerivce {

	private static final Logger logger = LoggerFactory.getLogger(P21TokenServiceImpl.class);

	@Value(IntegrationConstants.ERP_USERNAME)
	String USERNAME;

	@Value(IntegrationConstants.ERP_PASSWORD)
	String PASSWORD;

	@Value(IntegrationConstants.ERP_TOKEN_ENDPOINT)
	String TOKEN_ENDPOINT;

	@Override
	// @Cacheable(value = "accessTokenCache", key = "#accessToken")
	public String getToken() throws Exception {
		String accessToken = getAccessTokenFromCookie();
//		HttpResponse accessToken=null;
		logger.info("getAccessTokenFromCookie::" + accessToken);
		if (accessToken == null) {
			CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
			
			URIBuilder uriBuilder = new URIBuilder(TOKEN_ENDPOINT);
			uriBuilder.addParameter("username", USERNAME);
			uriBuilder.addParameter("password", PASSWORD);
			
            HttpPost request = new HttpPost(uriBuilder.build());
			
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

	private void setAccessTokenCookie(String accessToken) {
		logger.info("setAccessTokenCookie::" + accessToken);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		// Create a new cookie with the token
		Cookie cookie = new Cookie("accessToken", accessToken);

		// Set additional properties for the cookie (optional)
		cookie.setMaxAge(3600); // Set the expiration time in seconds
		cookie.setPath("/"); // Set the path for which the cookie is valid

		// Add the cookie to the response
		response.addCookie(cookie);

	}

	private static String parseAccessToken(String responseBody) throws Exception {
		// Create a JDOM Document from the XML response
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new StringReader(responseBody));

		// Get the root element of the document
		Element root = document.getRootElement();

		// Find the AccessToken element and extract its value
		Element accessTokenElement = root.getChild(IntegrationConstants.ACCESS_TOKEN_ELEMENT);
		String accessToken = accessTokenElement.getText();

		return accessToken;
	}
}
