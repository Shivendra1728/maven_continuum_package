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
import com.di.commons.dto.ReturnOrderItemDTO;
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

	@Value("${erp.rma.create}")
	String RMA_CREATE_API;
	
	
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
		P21ReturnOrderDataHelper p21ReturnOrderDataHelper = new P21ReturnOrderDataHelper();

		P21ReturnOrderHeaderHelper p21OrderHeader = new P21ReturnOrderHeaderHelper();
		p21OrderHeader.setCompany_id(returnOrderDTO.getCompanyId());
	    p21OrderHeader.setContact_id(returnOrderDTO.getContactId());
	    p21OrderHeader.setCustomer_id(returnOrderDTO.getCustomer().getCustomerId());
		p21OrderHeader.setPo_no(returnOrderDTO.getPONumber());
		p21OrderHeader.setSales_loc_id(returnOrderDTO.getSalesLocationId());
	    p21OrderHeader.setShip_to_id(returnOrderDTO.getShipTo().getId());
		
		p21OrderHeader.setTaker("Continuum");

		p21ReturnOrderDataHelper.setP21OrderHeader(p21OrderHeader);

		List<P21OrderItemHelper> p21OrderItemList = new ArrayList<>();
		List<String> reasonCodes = new ArrayList<>();
		for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderDTO.getReturnOrderItem()) {
			P21OrderItemHelper p21OrderItemHelper = new P21OrderItemHelper();
			p21OrderItemHelper.setOe_order_item_id(returnOrderItemDTO.getItemName());
			p21OrderItemHelper.setUnit_quantity(returnOrderItemDTO.getQuanity()+"");
			p21OrderItemList.add(p21OrderItemHelper);
			reasonCodes.add(returnOrderItemDTO.getReasonCode());
		}
		p21ReturnOrderDataHelper.setP21OrderItemList(p21OrderItemList);
		p21ReturnOrderDataHelper.setReasonCodes(reasonCodes);

		//P21OrderItemCustomerSalesHistory custSalesHistory = new P21OrderItemCustomerSalesHistory();
		//custSalesHistory.setOrder_no(returnOrderDTO.getOrderNo());
	//	custSalesHistory.setCc_invoice_no_display(returnOrderDTO.getInvoiceNo());
	//	custSalesHistory.setLocation_id(returnOrderDTO.getSalesLocationId());

		//p21ReturnOrderDataHelper.setP21OrderItemCustSalesHistory(custSalesHistory); //TODO invoice linking
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());

		String xmlPayload = p21ReturnOrderMarshller.createRMA(p21ReturnOrderDataHelper);
		System.out.println("returnOrderXmlPayload" + xmlPayload);
		headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = restTemplate.exchange(RMA_CREATE_API, HttpMethod.POST,
				new HttpEntity<>(xmlPayload, headers), String.class);
		String responseBody = response.getBody();

		System.out.println("#### RMA RESPONSE ####" + response.getBody());

		return responseBody;
	}

}