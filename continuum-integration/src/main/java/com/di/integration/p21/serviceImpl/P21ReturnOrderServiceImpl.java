package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.util.Collections;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.di.commons.dto.ReturnOrderDTO;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.P21ReturnOrderMarshller;
@Service
public class P21ReturnOrderServiceImpl implements P21ReturnOrderService {
	
	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	@Autowired
	P21ReturnOrderMarshller p21ReturnOrderMarshller;
	@Override
	public String createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		// TODO Auto-generated method stub
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		
		String returnOrderXmlPayload=p21ReturnOrderMarshller.prepareXml();
		System.out.println("returnOrderXmlPayload"+returnOrderXmlPayload);
		//URI fullURI = prepareOrderURI(orderSearchParameters);
		// Set the Accept header to receive JSON response
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// https://apiplay.labdepotinc.com/data/erp/views/v1/p21_view_ord_ack_hdr?$

		// Create the request entity with headers
		//RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, fullURI);

		// Make the API call
		//ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		//return  response.getBody();
		return returnOrderXmlPayload;
	}

}
