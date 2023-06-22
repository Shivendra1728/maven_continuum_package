package com.di.integration.p21.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.di.commons.dto.ReturnOrderDTO;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.P21OrderItemCustomerSalesHistory;
import com.di.integration.p21.transaction.P21OrderItemHelper;
import com.di.integration.p21.transaction.P21ReturnOrderDataHelper;
import com.di.integration.p21.transaction.P21ReturnOrderHeaderHelper;
import com.di.integration.p21.transaction.P21ReturnOrderMarshller;

@Service
public class P21ReturnOrderServiceImpl implements P21ReturnOrderService {

	@Value("${erp.data_api_base_url}")
	String DATA_API_BASE_URL;

	@Value("${erp.data_api_order_line}")
	String DATA_API_ORDER_LINE;

	// @Value("${erp.token}") //property also commented
	// String TOKEN;

	@Value("${erp.order_select_fields}")
	String ORDER_SELECT_FIELDS;

	@Value("${erp.order_format}")
	String ORDER_FORMAT;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	/*
	 * @Autowired TransactionSet transactionSet;
	 */

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	P21ReturnOrderMarshller p21ReturnOrderMarshller;

	@Autowired
	P21OrderLineServiceImpl p21orderLineServiceImpl;

	@Override
	public String createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		// TODO Auto-generated method stub
		P21ReturnOrderDataHelper p21ReturnOrderDataHelper = new P21ReturnOrderDataHelper();

		P21ReturnOrderHeaderHelper p21OrderHeader = new P21ReturnOrderHeaderHelper();
		p21OrderHeader.setCompany_id("LD001");
		p21OrderHeader.setContact_id("45560");
		p21OrderHeader.setCustomer_id("164977");
		p21OrderHeader.setPo_no("200000424");
		p21OrderHeader.setSales_loc_id("101");
		p21OrderHeader.setShip_to_id("164977");
		p21OrderHeader.setTaker("Continuum");

		p21ReturnOrderDataHelper.setP21OrderHeader(p21OrderHeader);

		P21OrderItemHelper p21OrderItemHelper = new P21OrderItemHelper();
		p21OrderItemHelper.setOe_order_item_id("FLOW-48");
		p21OrderItemHelper.setUnit_quantity("1");

		List<P21OrderItemHelper> p21OrderItemList = new ArrayList<>();
		p21OrderItemList.add(p21OrderItemHelper);
		p21ReturnOrderDataHelper.setP21OrderItemList(p21OrderItemList);

		String reasonCode = "RMA - BILL-TO CORRECTION";
		List<String> reasonCodes = new ArrayList<>();
		reasonCodes.add(reasonCode);

		p21ReturnOrderDataHelper.setReasonCodes(reasonCodes);

		P21OrderItemCustomerSalesHistory custSalesHistory = new P21OrderItemCustomerSalesHistory();
		custSalesHistory.setOrder_no("419063");
		custSalesHistory.setCc_invoice_no_display("1246655");
		custSalesHistory.setLocation_id("101");

		p21ReturnOrderDataHelper.setP21OrderItemCustSalesHistory(custSalesHistory);
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());

		String xmlPayload = p21ReturnOrderMarshller.createRMA(p21ReturnOrderDataHelper);
		System.out.println("returnOrderXmlPayload" + xmlPayload);
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> requestEntity = new HttpEntity<>(xmlPayload, headers);

		String apiUrl = "https://apiplay.labdepotinc.com/uiserver0/api/v2/transaction";

		// Send the POST request
//	        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
		ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST,
				new HttpEntity<>(xmlPayload, headers), String.class);

		// Create the HTTP entity with the XML payload and headers

		// Retrieve the response body
		String responseBody = response.getBody();

		System.out.println("### RMA RESPONSE####" + response.getBody());
		
		
		
		
		return responseBody;
	}

}
