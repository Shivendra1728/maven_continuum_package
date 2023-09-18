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
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.OrderNote;
import com.di.integration.p21.transaction.OrderXml;
import com.di.integration.p21.transaction.P21OrderItemHelper;
import com.di.integration.p21.transaction.P21RMAResponse;
import com.di.integration.p21.transaction.P21ReturnOrderDataHelper;
import com.di.integration.p21.transaction.P21ReturnOrderHeaderHelper;
import com.di.integration.p21.transaction.P21ReturnOrderMarshller;

@Service
public class P21ReturnOrderServiceImpl implements P21ReturnOrderService {

	private static final Logger logger = LoggerFactory.getLogger(P21ReturnOrderServiceImpl.class);

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String DATA_API_BASE_URL;

	@Value(IntegrationConstants.ERP_DATA_API_ORDER_LINE)
	String DATA_API_ORDER_LINE;

	// @Value("${erp.token}") //property also commented
	// String TOKEN;

	@Value(IntegrationConstants.ERP_ORDER_SELECT_FIELDS)
	String ORDER_SELECT_FIELDS;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Value(IntegrationConstants.ERP_RMA_CREATE_API)
	String RMA_CREATE_API;
	
	@Value("${erp.rma.notes.create}")
	String RMA_NOTES_CREATE_API;
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

		p21OrderHeader.setTaker(IntegrationConstants.CONTINUUM);

		p21ReturnOrderDataHelper.setP21OrderHeader(p21OrderHeader);

		List<P21OrderItemHelper> p21OrderItemList = new ArrayList<>();
		List<String> reasonCodes = new ArrayList<>();
		List<String> probDescList = new ArrayList<>();
		for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderDTO.getReturnOrderItem()) {
			P21OrderItemHelper p21OrderItemHelper = new P21OrderItemHelper();
			p21OrderItemHelper.setOe_order_item_id(returnOrderItemDTO.getItemName());
			p21OrderItemHelper.setUnit_quantity(returnOrderItemDTO.getQuanity() + "");
			p21OrderItemHelper.setNote(returnOrderItemDTO.getProblemDesc());
			p21OrderItemHelper.setLost_sales_id(returnOrderItemDTO.getReasonCode());
			p21OrderItemList.add(p21OrderItemHelper);
			reasonCodes.add(returnOrderItemDTO.getReasonCode());
			if (returnOrderItemDTO.getProblemDesc() != null) {
				probDescList.add(returnOrderItemDTO.getProblemDesc().replaceAll("\n", " "));
			}
		}
		p21ReturnOrderDataHelper.setP21OrderItemList(p21OrderItemList);
		p21ReturnOrderDataHelper.setReasonCodes(reasonCodes);
		p21ReturnOrderDataHelper.setProbDescList(probDescList);
		// P21OrderItemCustomerSalesHistory custSalesHistory = new
		// P21OrderItemCustomerSalesHistory();
		// custSalesHistory.setOrder_no(returnOrderDTO.getOrderNo());
		// custSalesHistory.setCc_invoice_no_display(returnOrderDTO.getInvoiceNo());
		// custSalesHistory.setLocation_id(returnOrderDTO.getSalesLocationId());

		// p21ReturnOrderDataHelper.setP21OrderItemCustSalesHistory(custSalesHistory);
		// //TODO invoice linking
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		logger.info("creating RMA");

		String xmlPayload = p21ReturnOrderMarshller.createRMA(p21ReturnOrderDataHelper);
		logger.info("returnOrderXmlPayload {}", xmlPayload);
		
		headers.setContentType(MediaType.APPLICATION_XML);
		ResponseEntity<String> response = restTemplate.exchange(RMA_CREATE_API, HttpMethod.POST,
				new HttpEntity<>(xmlPayload, headers), String.class);
		String responseBody = response.getBody();

		logger.info("#### RMA RESPONSE #### {}", response.getBody());
		P21RMAResponse rmaResponse=p21ReturnOrderMarshller.umMarshall(response.getBody().replace("Keys", "resKeys"));
		//Code for Return Order Notes
		
				OrderXml orderXml= new OrderXml();
				orderXml.setCompanyId(p21ReturnOrderDataHelper.getP21OrderHeader().getCompany_id());
				orderXml.setContactId(p21ReturnOrderDataHelper.getP21OrderHeader().getContact_id());
				orderXml.setCustomerId(p21ReturnOrderDataHelper.getP21OrderHeader().getCustomer_id());
				orderXml.setLocationId(p21ReturnOrderDataHelper.getP21OrderHeader().getSales_loc_id());
				orderXml.setOrderNo(rmaResponse.getRmaOrderNo());
				
				List<OrderNote> orderNotes= new ArrayList<>();
				for (P21OrderItemHelper orderItem : p21ReturnOrderDataHelper.getP21OrderItemList()) {
					OrderNote orderNote= new OrderNote();
					orderNote.setMandatory(true);
					orderNote.setOrderNo(orderXml.getOrderNo());
					orderNote.setNotepadClassId("OTHER");
					orderNote.setTopic("ORDER NOTE : "+orderItem.getOe_order_item_id());
					orderNote.setNote(orderItem.getNote());//reason code
					orderNotes.add(orderNote);
				}
				orderXml.setOrderNotes(orderNotes);
				String orderNoteXml=p21ReturnOrderMarshller.getXMLFromObject(orderXml);
				logger.info("RMA_NOTES_CREATE_API ::"+RMA_NOTES_CREATE_API);
				logger.info("Order Notes XML ::"+orderNoteXml);
				ResponseEntity<String> response1 = restTemplate.exchange(RMA_NOTES_CREATE_API, HttpMethod.POST,
						new HttpEntity<>(orderNoteXml, headers), String.class);
				String responseBody1 = response1.getBody();

				logger.info("#### RMA Notes RESPONSE #### {}", responseBody1);
		return rmaResponse;
	}

	@Override
	public P21RMAResponse linkInvoice() {
		// TODO Auto-generated method stub
		return null;
	}

}