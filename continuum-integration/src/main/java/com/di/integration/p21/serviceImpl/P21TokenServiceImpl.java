package com.di.integration.p21.serviceImpl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.di.integration.p21.service.P21TokenSerivce;

@Service
public class P21TokenServiceImpl implements P21TokenSerivce{

	@Value("${erp.username}")
	String USERNAME;
	
	@Value("${erp.password}")
	String PASSWORD;
	
	@Value("${erp.token_end_point}")
	String TOKEN_ENDPOINT;
	
		@Override
		//@Cacheable(value = "accessTokenCache", key = "#accessToken")
		public String getToken() throws Exception  {
	        	CloseableHttpClient httpClient = HttpClients.createDefault();
	            HttpPost request = new HttpPost(TOKEN_ENDPOINT);

	            // Set request parameters
	            List<NameValuePair> params = new ArrayList<>();
	            params.add(new BasicNameValuePair("grant_type", "password"));
	            params.add(new BasicNameValuePair("username", USERNAME));
	            params.add(new BasicNameValuePair("password", PASSWORD));
	           //params.add(new BasicNameValuePair("scope", SCOPE));

	            // Set request headers
	            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
	            request.setEntity(new UrlEncodedFormEntity(params));
	            // Execute the request
	            HttpResponse response = httpClient.execute(request);
	            HttpEntity entity = response.getEntity();
	            String responseBody = EntityUtils.toString(entity);

	            // Extract the access token from the response
	            // Assuming the response is in JSON format
	            // Adjust the parsing logic based on the actual response format
	            String accessToken = parseAccessToken(responseBody);
	            return accessToken;
	    }

	
	    
	    private static String parseAccessToken(String responseBody) throws Exception {
	        // Create a JDOM Document from the XML response
	        SAXBuilder builder = new SAXBuilder();
	        Document document = builder.build(new StringReader(responseBody));
	        
	        // Get the root element of the document
	        Element root = document.getRootElement();
	        
	        // Find the AccessToken element and extract its value
	        Element accessTokenElement = root.getChild("AccessToken");
	        String accessToken = accessTokenElement.getText();
	        
	        return accessToken;
	    }
	}


