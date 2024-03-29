package com.continuum.scheduler.task;

import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.scheduler.constants.ReceiptStatus;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.OrderItemDocuments;
import com.continuum.tenant.repos.entity.RMAReceiptInfo;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.OrderItemDocumentRepository;
import com.continuum.tenant.repos.repositories.RMAReceiptInfoRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
//import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.continuum.tenant.repos.repositories.RmaInvoiceInfoRepository;
import com.di.commons.dto.DocumentLinkDTO;
import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.di.commons.helper.DBContextHolder;
import com.di.commons.helper.DocumentLinkHelper;
import com.di.commons.mapper.RmaInvoiceInfoMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21DocumentService;
import com.di.integration.p21.service.P21InvoiceService;
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;
import com.di.integration.p21.serviceImpl.RmaReceiptServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21InvoiceLinkTaskScheduler {

	private static final Logger logger = LoggerFactory.getLogger(P21InvoiceLinkTaskScheduler.class);
//	
//	@Autowired
//	P21InvoiceService p21InvoiceService;
//	

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ReturnOrderRepository returnOrderRepository;
	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;
	@Autowired
	RmaInvoiceInfoRepository rmaInvoiceInfoRepository;
	@Autowired
	P21InvoiceService p21InvoiceService;
	@Autowired
	RmaInvoiceInfoMapper rmaInvoiceInfoMapper;
	@Autowired
	MasterTenantRepository masterTenantRepo;
	@Autowired
	OrderItemDocumentRepository orderItemDocumentRepository;
	@Autowired
	P21DocumentService p21DocumentService;
	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	@Autowired
	RMAReceiptInfoRepository rmaReceiptInfoRepository;
	@Autowired
	RmaReceiptServiceImpl rmaReceiptServiceImpl;
	@Autowired
	AuditLogRepository auditLogRepository;

	@Scheduled(cron = "0 0/10 * * * *")
	public void runTasks() throws Exception {
		List<MasterTenant> masterTenants = masterTenantRepo.findAll();
		if (null == masterTenants) {
			logger.error("An error during getting tenant name");
			throw new BadCredentialsException("Invalid tenant and user.");
		}
		for (MasterTenant masterTenant : masterTenants) {
			DBContextHolder.setCurrentDb(masterTenant.getDbName());
			linkSalesRep(masterTenant);
			linkInvoice(masterTenant);
		}

	}

	@Scheduled(cron = "0 */5 * * * *")
	public void createRMAReciept() throws Exception {

		DBContextHolder.setCurrentDb("pace");
		masterTenantRepo.findByDbName(DBContextHolder.getCurrentDb());
		createRMAreceipt(masterTenantRepo.findByDbName(DBContextHolder.getCurrentDb()));

	}

	private void createRMAreceipt(MasterTenant masterTenant) throws Exception {
		logger.info("Creating Receipts-------------------");
		List<RMAReceiptInfo> findByStatus = rmaReceiptInfoRepository
				.findByStatusAndRetryCountLessThan(ReceiptStatus.SCHEDULED.toString(), 4);
		if (findByStatus != null && !findByStatus.isEmpty()) {
			for (RMAReceiptInfo rmaReceiptInfo : findByStatus) {
				rmaReceiptInfo.setStatus(ReceiptStatus.INPROGRESS.toString());
				rmaReceiptInfo.setRetryCount(rmaReceiptInfo.getRetryCount() + 1);
				rmaReceiptInfoRepository.save(rmaReceiptInfo);
				Map<String, List<String>> rmaReceipts = rmaReceiptServiceImpl
						.createRmaReceipt(rmaReceiptInfo.getRmaNo(), masterTenant);
				if (rmaReceipts != null && !rmaReceipts.isEmpty()) {
					String description = "";
					String itemsString = "";
					String receiptNumbers = "";
					for (String receiptNo : rmaReceipts.keySet()) {
						receiptNumbers += receiptNo+", ";
						itemsString = String.join(", ", rmaReceipts.get(receiptNo));
						description += "Receipt created successfully for item " + itemsString
								+ " your receipt number is : " + receiptNo + ";";
					}
					saveAuditLog(rmaReceiptInfo, description);
					rmaReceiptInfo.setStatus(ReceiptStatus.SUCCEED.toString());
					rmaReceiptInfo.setReceiptNo(receiptNumbers);
					rmaReceiptInfoRepository.save(rmaReceiptInfo);
				} else {
					if (rmaReceiptInfo.getRetryCount() >= 3) {
						rmaReceiptInfo.setStatus(ReceiptStatus.FAILED.toString());
						rmaReceiptInfoRepository.save(rmaReceiptInfo);
					}
				}
			}
		}
	}

	private void saveAuditLog(RMAReceiptInfo rmaReceiptInfo, String description) {
		AuditLog auditLog = new AuditLog();
		auditLog.setTitle("Inbox");
		auditLog.setDescription(description);
		auditLog.setHighlight("");
		auditLog.setStatus("RMA Header");
		auditLog.setRmaNo(rmaReceiptInfo.getRmaNo());
		auditLog.setUserName("");
		auditLogRepository.save(auditLog);
	}

	public void linkInvoice(MasterTenant masterTenant) throws Exception {

		logger.info("In  document linking started");
		List<RmaInvoiceInfo> rma = rmaInvoiceInfoRepository.findAll();

		List<RmaInvoiceInfoDTO> rmaDTOList = new ArrayList<>();

		for (RmaInvoiceInfo rmaInvoiceInfo : rma) {
			try {
				if (!rmaInvoiceInfo.isDocumentLinked() && rmaInvoiceInfo.getRetryCount() < 3) {
					linkDocuments(rmaInvoiceInfo, masterTenant);
					rmaInvoiceInfo.setDocumentLinked(true);
					Optional<ReturnOrder> roo = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());
					if (roo.isPresent()) {
						ReturnOrder RO = roo.get();
						RO.setISDocumentLinked(true);
						returnOrderRepository.save(RO);
					}
				}

			} catch (Exception e) {
				logger.error("error in document linking");
				rmaInvoiceInfo.setDocumentLinked(false);
			}
			RmaInvoiceInfoDTO rmaInvoiceInfoDTO = new RmaInvoiceInfoDTO();

			// Manually set properties of rmaInvoiceInfoDTO based on rmaInvoiceInfo
			rmaInvoiceInfoDTO.setRmaOrderNo(rmaInvoiceInfo.getRmaOrderNo());
			rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfo.getRetryCount());
			// Set other properties as needed

			rmaDTOList.add(rmaInvoiceInfoDTO);

			String rmaOrderNo = rmaInvoiceInfoDTO.getRmaOrderNo();
			Integer retryCount = rmaInvoiceInfoDTO.getRetryCount();
			if (retryCount < 3) {
				logger.info("In  document linking started");
				boolean bln = p21InvoiceService.linkInvoice(rmaOrderNo, masterTenant);
				if (bln) {
					Optional<ReturnOrder> ro = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());

					if (ro.isPresent()) {

						ReturnOrder returnOrder = ro.get();
						returnOrder.setISInvoiceLinked(true);
						returnOrderRepository.save(returnOrder);
						rmaInvoiceInfoRepository.delete(rmaInvoiceInfo);
						// Here you can remove the corresponding DTO from the list

					}
				} else {
					rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfoDTO.getRetryCount() + 1);
					rmaInvoiceInfoDTO.setInvoiceLinked(false);

					RmaInvoiceInfo rii = rmaInvoiceInfoMapper.RmaInvoiceInfoToRmaInvoiceInfoDTO(rmaInvoiceInfoDTO);
					rmaInvoiceInfoRepository.save(rii);
				}
			}
		}

	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

	private boolean linkDocuments(RmaInvoiceInfo rmaInvoiceInfo, MasterTenant masterTenant) throws Exception {
		boolean docLinked = false;

		Optional<ReturnOrder> ro = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());
		ReturnOrder returnOrder = ro.get();

		List<ReturnOrderItem> returnOrderItemList = returnOrderItemRepository.findByReturnOrderId(returnOrder.getId());

		DocumentLinkDTO docmentLinkDto = new DocumentLinkDTO();
		docmentLinkDto.setRmaNo(rmaInvoiceInfo.getRmaOrderNo());
		List<DocumentLinkHelper> documentList = new ArrayList();

		for (ReturnOrderItem returnOrderItem : returnOrderItemList) {
			List<OrderItemDocuments> orderItemDocuments = orderItemDocumentRepository
					.findByReturnOrderItem_Id(returnOrderItem.getId());

			for (OrderItemDocuments orderItemDocument : orderItemDocuments) {
				if (isNotNullAndNotEmpty(orderItemDocument.getURL())) {
					String fileName = Paths.get(new URI(orderItemDocument.getURL()).getPath()).getFileName().toString();
					documentList.add(new DocumentLinkHelper(fileName, orderItemDocument.getURL()));
				}

			}
		}

		docmentLinkDto.setDocumentLinkHelperList(documentList);
		p21DocumentService.linkDocument(docmentLinkDto, masterTenant);

		docLinked = true;
		return docLinked;

	}

	// Implementing Sales Rep scheduled -

	private String linkSalesRep(MasterTenant masterTenant) throws Exception {
		// TODO Auto-generated method stub
		logger.info("Sales Rep linking started");

		List<ReturnOrder> unlinkedRMAs = returnOrderRepository.findByIsSalesRepLinkedFalse();

		for (ReturnOrder lastRma : unlinkedRMAs) {

			if (lastRma.getSalesRepLinkAttempts() != null && lastRma.getSalesRepLinkAttempts() >= 2) {
				logger.error("Maximum attempts reached for RMA: " + lastRma.getRmaOrderNo());
				continue;
			}

			if (lastRma == null || (lastRma.getIsSalesRepLinked() != null && lastRma.getIsSalesRepLinked())) {
				logger.info(
						"Sales rep is already linked for RMA: " + (lastRma != null ? lastRma.getRmaOrderNo() : "N/A"));
				return "Linked already";
			}
			logger.info("Processing unlinked RMA: " + lastRma.getRmaOrderNo());
			logger.info("This is last RMA in return order table :: " + lastRma.getRmaOrderNo());

			String orderNoStr = lastRma.getOrderNo();
			int orderNo = Integer.parseInt(orderNoStr);

			String apiURL = masterTenant.getSubdomain() + "/api/sales/orders/" + orderNo
					+ "?extendedproperties=Salesreps";

			logger.info("This is the original order number : : " + orderNo);

//			String apiPayload = String.format(
//					"{\"ServiceName\":\"Order\",\"TransactionStates\":[{\"DataElementName\":\"TABPAGE_1.order\",\"Keys\":[{\"Name\":\"order_no\",\"Value\":%d}]}],\"UseCodeValues\":true}",
//					orderNo);

			// logger.info("Making post request to get the Primary Sales Rep : : " +
			// apiPayload);
			String responseJson = makeGetRequest(apiURL, masterTenant);

			logger.info("Parsing primary sales rep :: " + responseJson);

			String primarySalesRep = parsePrimarySalesRep(responseJson);

			logger.info("This is primary Sales Rep for this order :: " + orderNo + " :: " + primarySalesRep);

			if (SetPrimarySalesRepInRma(lastRma, primarySalesRep, masterTenant)) {
				// If sales rep linking was successful
				lastRma.setSalesRepLinkAttempts(0);
				returnOrderRepository.save(lastRma);
				return "Primary sales rep set complete : :" + primarySalesRep;
			} else {
				// If sales rep linking failed
				if (lastRma.getSalesRepLinkAttempts() == null) {
					lastRma.setSalesRepLinkAttempts(1);
				} else {
					lastRma.setSalesRepLinkAttempts(lastRma.getSalesRepLinkAttempts() + 1);
				}
				returnOrderRepository.save(lastRma);
				return "Sales rep linking failed for RMA: " + lastRma.getRmaOrderNo() + ". Retrying...";
			}
		}
		return "Sales Rep setting complete for rma's : : " + unlinkedRMAs;
	}

	// These methods will help you find primary Sales Rep against order
	// ----------------------------------------

	private boolean SetPrimarySalesRepInRma(ReturnOrder lastRma, String primarySalesRep, MasterTenant masterTenant)
			throws JsonMappingException, JsonProcessingException {

		int rmaNumber = Integer.parseInt(lastRma.getRmaOrderNo());

		logger.info("This is rma number : : " + rmaNumber);

		String SalesRepInRMA = masterTenant.getSubdomain() + IntegrationConstants.ERP_RMA_ENDPOINT;

		logger.info("This is sales Rep api in RMA : : " + SalesRepInRMA);

		String payload = String.format("{\n" + "    \"IgnoreDisabled\": true,\n" + "    \"Name\": \"RMA\",\n"
				+ "    \"UseCodeValues\": false,\n" + "    \"Transactions\": [\n" + "        {\n"
				+ "            \"Status\": \"New\",\n" + "            \"DataElements\": [\n" + "                {\n"
				+ "                    \"Name\": \"TABPAGE_1.order\",\n" + "                    \"Type\": \"Form\",\n"
				+ "                    \"Keys\": [],\n" + "                    \"Rows\": [\n"
				+ "                        {\n" + "                            \"Edits\": [\n"
				+ "                                {\n"
				+ "                                    \"Name\": \"order_no\",\n"
				+ "                                    \"Value\": %d\n" + "                                }\n"
				+ "                            ],\n" + "                            \"RelativeDateEdits\": []\n"
				+ "                        }\n" + "                    ]\n" + "                },\n"
				+ "                {\n" + "                    \"Name\": \"TP_SALESREPS.tp_salesreps\",\n"
				+ "                    \"Type\": \"List\",\n" + "                    \"Keys\": [\n"
				+ "                        \"salesrep_id\"\n" + "                    ],\n"
				+ "                    \"Rows\": [\n" + "                        {\n"
				+ "                            \"Edits\": [\n" + "                                {\n"
				+ "                                    \"Name\": \"salesrep_id\",\n"
				+ "                                    \"Value\": \"%s\"\n" + "                                },\n"
				+ "                                {\n"
				+ "                                    \"Name\": \"primary_salesrep\",\n"
				+ "                                    \"Value\": \"Y\"\n" + "                                }\n"
				+ "                            ],\n" + "                            \"RelativeDateEdits\": []\n"
				+ "                        }\n" + "                    ]\n" + "                }\n" + "            ]\n"
				+ "        }\n" + "    ]\n" + "}", rmaNumber, primarySalesRep);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			headers.setBearerAuth(p21TokenServiceImpl.findToken(masterTenant));
		} catch (Exception e) {
			e.printStackTrace();
		}
		HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(SalesRepInRMA, HttpMethod.POST, requestEntity,
				String.class);

		HttpStatus statusCode = response.getStatusCode();

		String responseBody = response.getBody();

		logger.info("This is status code " + statusCode + "This is response from setting sales rep in rma : : "
				+ responseBody);

		if (statusCode == HttpStatus.OK) {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);

			// Check if the summary succeeded count is 1
			int succeededCount = jsonNode.path("Summary").path("Succeeded").asInt();
			if (succeededCount == 1) {
				logger.info("Sales rep linked successfully");

				lastRma.setIsSalesRepLinked(true);

				returnOrderRepository.save(lastRma);
				return true;
			} else {
				logger.error("Sales rep linking failed, I guess we didn't get succeed count as one.");
			}
		} else {
			logger.error("Failed to make API call. Status code: " + statusCode);
		}
		return false;

	}

	private String makeGetRequest(String URI, MasterTenant masterTenant) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.findToken(masterTenant));
		logger.info("API URI: " + URI);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		RequestEntity<String> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, new URI(URI));

		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
		return response.getBody();
	}

	public static String parsePrimarySalesRep(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject salesreps = jsonObject.getJSONObject("Salesreps");
			JSONArray salesrepList = salesreps.getJSONArray("list");

			for (int i = 0; i < salesrepList.length(); i++) {
				JSONObject salesrep = salesrepList.getJSONObject(i);
				boolean isPrimarySalesrep = salesrep.getBoolean("PrimarySalesrep");

				if (isPrimarySalesrep) {
					return salesrep.getString("SalesrepId");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// THESE METHODS WILL HELP YOU GET THE PRIMARY SALES
	// REP-----------------------------------------------------------------

}