package com.di.integration.p21.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Value(IntegrationConstants.ERP_ORDER_FORMAT)
	String ORDER_FORMAT;

	@Value(IntegrationConstants.ERP_RMA_CREATE_API)
	String RMA_CREATE_API;

	@Value("${erp.rma.notes.create}")
	String RMA_NOTES_CREATE_API;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

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
		String xmlPayload = p21ReturnOrderMarshller.createRMA(p21ReturnOrderDataHelper);
		logger.info("returnOrderXmlPayload {}", xmlPayload);

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request = new HttpPost(masterTenant.getSubdomain() + RMA_CREATE_API);

		// Set request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity = new StringEntity(xmlPayload);
		request.setEntity(entity);
		CloseableHttpResponse response = httpClient.execute(request);
		String responseBody = EntityUtils.toString(response.getEntity());

		logger.info("#### RMA RESPONSE #### {}", responseBody);
		P21RMAResponse rmaResponse = p21ReturnOrderMarshller.umMarshall(responseBody.replace("Keys", "resKeys"));
		// Code for Return Order Notes

		OrderXml orderXml = new OrderXml();
		orderXml.setCompanyId(p21ReturnOrderDataHelper.getP21OrderHeader().getCompany_id());
		orderXml.setContactId(p21ReturnOrderDataHelper.getP21OrderHeader().getContact_id());
		orderXml.setCustomerId(p21ReturnOrderDataHelper.getP21OrderHeader().getCustomer_id());
		orderXml.setLocationId(p21ReturnOrderDataHelper.getP21OrderHeader().getSales_loc_id());
		orderXml.setOrderNo(rmaResponse.getRmaOrderNo());

		List<OrderNote> orderNotes = new ArrayList<>();
		for (P21OrderItemHelper orderItem : p21ReturnOrderDataHelper.getP21OrderItemList()) {
			OrderNote orderNote = new OrderNote();
			orderNote.setMandatory(true);
			orderNote.setOrderNo(orderXml.getOrderNo());
			orderNote.setNotepadClassId("OTHER");
			orderNote.setTopic("ORDER NOTE : " + orderItem.getOe_order_item_id());
			orderNote.setNote(orderItem.getNote());// reason code
			orderNotes.add(orderNote);
		}
		orderXml.setOrderNotes(orderNotes);
		String orderNoteXml = p21ReturnOrderMarshller.getXMLFromObject(orderXml);
		logger.info("RMA_NOTES_CREATE_API ::" + RMA_NOTES_CREATE_API);
		logger.info("Order Notes XML ::" + orderNoteXml);

		CloseableHttpClient httpClient1 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request1 = new HttpPost(masterTenant.getSubdomain() + RMA_NOTES_CREATE_API);

		// Set request headers
		request1.setHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity1 = new StringEntity(xmlPayload);
		request1.setEntity(entity1);
		CloseableHttpResponse response1 = httpClient1.execute(request1);
		String responseBody1 = EntityUtils.toString(response1.getEntity());

		logger.info("#### RMA Notes RESPONSE #### {}", responseBody1);

		// Code to put carrier from original order to RMA

		String requestBody = "{\n" + "    \"IgnoreDisabled\": true,\n" + "    \"Name\": \"RMA\",\n"
				+ "    \"UseCodeValues\": false,\n" + "    \"Transactions\": [\n" + "        {\n"
				+ "            \"Status\": \"New\",\n" + "            \"DataElements\": [\n" + "                {\n"
				+ "                    \"Name\": \"TABPAGE_1.order\",\n" + "                    \"Type\": \"Form\",\n"
				+ "                    \"Keys\": [],\n" + "                    \"Rows\": [\n"
				+ "                        {\n" + "                            \"Edits\": [\n"
				+ "                                {\n"
				+ "                                    \"Name\": \"order_no\",\n"
				+ "                                    \"Value\": " + rmaResponse.getRmaOrderNo() + "\n"
				+ "                                }\n" + "                            ],\n"
				+ "                            \"RelativeDateEdits\": []\n" + "                        }\n"
				+ "                    ]\n" + "                },\n" + "                {\n"
				+ "                    \"Name\": \"TP_SHIPINFO.shipinfo\",\n"
				+ "                    \"Type\": \"Form\",\n" + "                    \"Keys\": [],\n"
				+ "                    \"Rows\": [\n" + "                        {\n"
				+ "                            \"Edits\": [\n" + "                                {\n"
				+ "                                    \"Name\": \"oe_hdr_carrier_id\",\n"
				+ "                                    \"Value\": \"" + returnOrderDTO.getCarrierName() + "\"\n"
				+ "                                }\n" + "                            ],\n"
				+ "                            \"RelativeDateEdits\": []\n" + "                        }\n"
				+ "                    ]\n" + "                }\n" + "            ]\n" + "        }\n" + "    ]\n"
				+ "}";

		CloseableHttpClient httpClient2 = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request2 = new HttpPost(masterTenant.getSubdomain() + RMA_CREATE_API);

		logger.info("This is request for carrier setting in RMA" + request2);

		request2.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request2.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request2.addHeader(HttpHeaders.ACCEPT, "application/json");

		request2.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

		try (CloseableHttpResponse response2 = httpClient2.execute(request2)) {
			HttpEntity entity2 = response2.getEntity();
			String responseString = EntityUtils.toString(entity2);
			int isCarrierSet = parseResponse(responseString);
			rmaResponse.setCarrierSucceded(isCarrierSet);
		}

		return rmaResponse;
	}

	private int parseResponse(String responseString) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(responseString);

			JsonNode summaryNode = rootNode.path("Summary");
			int succeededCount = summaryNode.path("Succeeded").asInt();

			return succeededCount;
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Carrier Setting is Failed.");
			return 0;
		}
	}

	public String generateRmaNumber() {
		String prefix = "FAIL";

		ReturnOrder lastCRDRecord = returnOrderRepository
				.findFirstByRmaOrderNoStartingWithOrderByRmaOrderNoDesc(prefix);

		if (lastCRDRecord == null) {
			int startingValue = 1;
			return String.format("%s%07d", prefix, startingValue);
		} else {
			String rma = lastCRDRecord.getRmaOrderNo();
			int number = Integer.parseInt(rma.substring(prefix.length()));
			number++;
			return String.format("%s%07d", prefix, number);
		}
	}

	@Override
	public P21RMAResponse linkInvoice() {
		// TODO Auto-generated method stub
		return null;
	}

}