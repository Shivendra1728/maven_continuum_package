package com.di.integration.p21.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.di.integration.p21.transaction.P21OrderItemHelper;
import com.di.integration.p21.transaction.P21RMAResponse;
import com.di.integration.p21.transaction.P21ReturnOrderDataHelper;
import com.di.integration.p21.transaction.P21ReturnOrderHeaderHelper;
import com.di.integration.p21.transaction.P21ReturnOrderMarshller;

@Service
public class P21ReturnOrderServiceImpl implements P21ReturnOrderService {
	
	private static final Logger logger = LoggerFactory.getLogger(P21ReturnOrderServiceImpl.class);

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
	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		
		
		P21ReturnOrderDataHelper p21ReturnOrderDataHelper = new P21ReturnOrderDataHelper();

		P21ReturnOrderHeaderHelper p21OrderHeader = new P21ReturnOrderHeaderHelper();
		p21OrderHeader.setCompany_id(returnOrderDTO.getCompanyId());
	    p21OrderHeader.setContact_id(returnOrderDTO.getContactId());
	    p21OrderHeader.setCustomer_id(returnOrderDTO.getCustomer().getCustomerId());
		p21OrderHeader.setPo_no(returnOrderDTO.getPONumber());
		p21OrderHeader.setSales_loc_id(returnOrderDTO.getSalesLocationId());
	    p21OrderHeader.setShip_to_id(returnOrderDTO.getShipTo().getAddressId());
		
		p21OrderHeader.setTaker("Continuum");

		p21ReturnOrderDataHelper.setP21OrderHeader(p21OrderHeader);

		List<P21OrderItemHelper> p21OrderItemList = new ArrayList<>();
		List<String> reasonCodes = new ArrayList<>();
		List<String> probDescList = new ArrayList<>();
		for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderDTO.getReturnOrderItem()) {
			P21OrderItemHelper p21OrderItemHelper = new P21OrderItemHelper();
			p21OrderItemHelper.setOe_order_item_id(returnOrderItemDTO.getItemName());
			p21OrderItemHelper.setUnit_quantity(returnOrderItemDTO.getQuanity()+"");
			p21OrderItemHelper.setNote(returnOrderItemDTO.getProblemDesc());
			p21OrderItemHelper.setLost_sales_id(returnOrderItemDTO.getReasonCode());
			p21OrderItemList.add(p21OrderItemHelper);
			reasonCodes.add(returnOrderItemDTO.getReasonCode());
			if(returnOrderItemDTO.getProblemDesc()!=null) {
				probDescList.add(returnOrderItemDTO.getProblemDesc().replaceAll("\n", " "));
			}
		}
		p21ReturnOrderDataHelper.setP21OrderItemList(p21OrderItemList);
		p21ReturnOrderDataHelper.setReasonCodes(reasonCodes);
		p21ReturnOrderDataHelper.setProbDescList(probDescList);
		//P21OrderItemCustomerSalesHistory custSalesHistory = new P21OrderItemCustomerSalesHistory();
		//custSalesHistory.setOrder_no(returnOrderDTO.getOrderNo());
	//	custSalesHistory.setCc_invoice_no_display(returnOrderDTO.getInvoiceNo());
	//	custSalesHistory.setLocation_id(returnOrderDTO.getSalesLocationId());

		//p21ReturnOrderDataHelper.setP21OrderItemCustSalesHistory(custSalesHistory); //TODO invoice linking
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		logger.info("creating RMA");
		
		String xmlPayload = p21ReturnOrderMarshller.createRMA(p21ReturnOrderDataHelper);
		logger.info("returnOrderXmlPayload {}", xmlPayload);

		headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = restTemplate.exchange(RMA_CREATE_API, HttpMethod.POST,
				new HttpEntity<>(xmlPayload, headers), String.class);
		String responseBody = response.getBody();
		
		
		 logger.info("#### RMA RESPONSE #### {}", response.getBody().replace("Keys", "resKeys"));

		return	p21ReturnOrderMarshller.umMarshall( response.getBody().replace("Keys", "resKeys"));
	}

}