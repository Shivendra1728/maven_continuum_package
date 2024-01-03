package com.continuum.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.constants.PortalConstants;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.service.AuditLogService;
import com.continuum.service.ReturnOrderItemService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.EditableConfig;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.ReturnRoom;
import com.continuum.tenant.repos.entity.StatusConfig;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.continuum.tenant.repos.repositories.EditableConfigRepository;
import com.continuum.tenant.repos.repositories.QuestionConfigRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.continuum.tenant.repos.repositories.ReturnRoomRepository;
import com.continuum.tenant.repos.repositories.StatusConfigRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.commons.mapper.ReturnOrderItemMapper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21UpdateRMAService;
import com.di.integration.p21.serviceImpl.P21SKUServiceImpl;
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;
import com.di.integration.p21.serviceImpl.P21UpdateRMAServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class ReturnOrderItemServiceImpl implements ReturnOrderItemService {
	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderItemServiceImpl.class);

	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuditLogRepository auditLogRepository;
	@Autowired
	QuestionConfigRepository questionConfigRepository;

	@Autowired
	P21SKUServiceImpl p21SKUServiceImpl;

	@Autowired
	ReturnRoomRepository returnRoomRepository;

	@Autowired
	StatusConfigRepository statusConfigRepository;

	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EmailSender emailSender;

	@Autowired
	P21UpdateRMAService p21UpdateRMAService;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	AuditLogServiceImpl auditLogServiceImpl;

	@Value(PortalConstants.MAIL_HOST)
	private String mailHost;

	@Value(PortalConstants.MAIL_PORT)
	private int mailPort;

	@Value(PortalConstants.MAIL_USERNAME)
	private String mailUsername;

	@Value(PortalConstants.EMAIL_RECIPIENT)
	private String recipient;

	@Value(PortalConstants.MAIL_PASSWORD)
	private String mailPassword;

	EmailTemplateRenderer emailTemplateRenderer = new EmailTemplateRenderer();

	@Autowired
	ReturnOrderServiceImpl returnOrderServiceImpl;

	@Autowired
	AuditLogService auditLogService;

	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	private String rmaGetEndPoint;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	EditableConfigRepository editableConfigRepository;

	@Autowired
	P21UpdateRMAServiceImpl p21UpdateRMAServiceImpl;

	@Autowired
	ReturnOrderItemMapper returnOrderItemMapper;

	@Override
	public String updateReturnOrderItem(Long id, String rmaNo, String updateBy, ReturnOrderItemDTO updatedItem) {

		final Logger logger = LoggerFactory.getLogger(ReturnOrderItemServiceImpl.class);

		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(id);
		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(rmaNo);
		ReturnOrder returnOrder = findByRmaOrderNo.get();
		if (optionalItem.isPresent()) {
			AuditLog auditLog = new AuditLog();
			ReturnOrderItem existingItem = optionalItem.get();
			String existingUpdatedStatus = existingItem.getStatus();

			String previousStatus = existingItem.getStatus();
			String previousRC = existingItem.getReasonCode();
			String previousPD = existingItem.getProblemDesc();
			String TrackingUrl = existingItem.getTrackingUrl();
			String TrackingNumber = existingItem.getTrackingNumber();
			String CourierName = existingItem.getCourierName();

			// Update only the fields that are not null in updatedItem
			if (updatedItem.getProblemDescNote() != null || updatedItem.getProblemDesc() != null) {
				existingItem.setProblemDescNote(updatedItem.getProblemDescNote());
				existingItem.setProblemDesc(updatedItem.getProblemDesc());
				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(updatedItem.getProblemDescNote());
				returnRoom.setReturnOrderItem(existingItem);
				returnRoom.setAssignTo(null);
				returnRoomRepository.save(returnRoom);
				returnOrderItemRepository.save(existingItem);

			}

			if (updatedItem.getAmount() != null || updatedItem.getAmountNote() != null) {
				BigDecimal existingAmount = existingItem.getAmount();
				String existingAmountNote = existingItem.getAmountNote();

				String description = "";
				String title = "Update Activity";
				String highlight = "";
				String status = "Line Items";

				if (updatedItem.getAmount() != null) {
					existingItem.setAmount(updatedItem.getAmount());
					if (existingItem.getReStockingAmount() != null) {
						BigDecimal newRefundAmount = updatedItem.getAmount()
								.subtract(existingItem.getReStockingAmount());
						existingItem.setReturnAmount(newRefundAmount);
					}

				}

//				if (existingItem.getReturnAmount() != null
//						&& existingItem.getReturnAmount() != BigDecimal.valueOf(0)) {
//					BigDecimal newRefundAmount = updatedItem.getAmount()
//							.subtract(existingItem.getReStockingAmount());
//					existingItem.setReturnAmount(newRefundAmount);
//				}

				if (!updatedItem.getAmountNote().isEmpty() && !updatedItem.getAmountNote().equals("")) {
					existingItem.setAmountNote(updatedItem.getAmountNote());
					if (updatedItem.getAmountNote() != null && !updatedItem.getAmountNote().equals("")) {
						ReturnRoom returnRoom = new ReturnRoom();
						returnRoom.setName(updateBy);
						returnRoom.setMessage(updatedItem.getAmountNote());
						returnRoom.setReturnOrderItem(existingItem);
						returnRoom.setAssignTo(null);
						returnRoomRepository.save(returnRoom);
					}
				}
				List<String> updates = new ArrayList<>();

				if (updatedItem.getAmount().compareTo(existingAmount) != 0) {
					updates.add("Amount has been updated of item - " + existingItem.getItemName() + " from $"
							+ existingAmount + " to $" + updatedItem.getAmount());
				}

				if (!updatedItem.getAmountNote().isEmpty() && !updatedItem.getAmountNote().equals("")
						&& existingAmountNote != null) {
					updates.add("Amount Note has been updated of item - " + existingItem.getItemName() + " from " + "'"
							+ existingAmountNote + "'" + " to " + "'" + updatedItem.getAmountNote() + "'");
				}

				if (existingAmountNote == null && updatedItem.getAmount() != null
						&& updatedItem.getAmount().equals(existingAmount)) {
					updates.add("Amount Note has been updated of item - " + existingItem.getItemName() + " to " + "'"
							+ updatedItem.getAmountNote() + "'");
				}

				if (!updates.isEmpty()) {
					description = String.join(" by " + updateBy + ".;", updates) + ".";
					highlight = "Amount";

					auditLogServiceImpl.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
					returnOrderItemRepository.save(existingItem);
					if (existingUpdatedStatus.equalsIgnoreCase(PortalConstants.AUTHORIZED_AWAITING_TRANSIT)
							|| existingUpdatedStatus.equalsIgnoreCase(PortalConstants.AUTHORIZED_IN_TRANSIT)
							|| existingUpdatedStatus.equalsIgnoreCase(PortalConstants.RECIEVED)) {
						sendAmountToErp(rmaNo, existingItem);
					}

				}
			}

			if (updatedItem.getTrackingNumber() != null || updatedItem.getCourierName() != null
					|| updatedItem.getTrackingUrl() != null) {
				String existingTrackingNumber = existingItem.getTrackingNumber();
				String existingTrackingUrl = existingItem.getTrackingUrl();
				String existingCourierName = existingItem.getCourierName();

				String description = "";
				String highlight = "";
				String title = "Update Activity";
				String status = "Line Items";
				if (!updatedItem.getTrackingNumber().isEmpty()) {
					existingItem.setTrackingNumber(updatedItem.getTrackingNumber());
				}

				if (!updatedItem.getTrackingUrl().isEmpty()) {
					existingItem.setTrackingUrl(updatedItem.getTrackingUrl());
				}

				if (!updatedItem.getCourierName().isEmpty()) {
					existingItem.setCourierName(updatedItem.getCourierName());
				}

				List<String> updates = new ArrayList<>();
				if (!updatedItem.getTrackingNumber().isEmpty()
						&& !updatedItem.getTrackingNumber().equalsIgnoreCase(existingTrackingNumber)) {
					updates.add("Tracking Number has been updated of item - " + existingItem.getItemName() + " to "
							+ updatedItem.getTrackingNumber());
				}

				if (!updatedItem.getTrackingUrl().isEmpty()
						&& !updatedItem.getTrackingUrl().equalsIgnoreCase(existingTrackingUrl)) {
					updates.add("Tracking Url has been updated of item - " + existingItem.getItemName() + " to "
							+ updatedItem.getTrackingUrl());
				}

				if (!updatedItem.getCourierName().isEmpty()
						&& !updatedItem.getCourierName().equalsIgnoreCase(existingCourierName)) {
					updates.add("Courier Name has been updated of item - " + existingItem.getItemName() + " to "
							+ updatedItem.getCourierName());
				}

				description = String.join(" by " + updateBy + ".;", updates) + ".";
				highlight = "Tracking";

				auditLogServiceImpl.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
				returnOrderItemRepository.save(existingItem);

			}
			if (updatedItem.getStatus() != null) {
				String existingStatus = existingItem.getStatus();
				existingItem.setStatus(updatedItem.getStatus());
				BigDecimal existingAmount = BigDecimal.ZERO;
				BigDecimal existingRestockingFee = BigDecimal.ZERO;
				if (existingItem.getAmount() != null && existingItem.getReStockingAmount() != null) {
					existingAmount = existingItem.getAmount();
					existingRestockingFee = existingItem.getReStockingAmount();
				}

				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_AWAITING_TRANSIT)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_IN_TRANSIT)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_CANCLED)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_LINE_DENIED)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_DENIED)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RECIEVED)) {
					List<StatusConfig> statusConfigList = statusConfigRepository
							.findBystatuslabl(updatedItem.getStatus());
					StatusConfig statusConfig = statusConfigList.get(0);
					System.err.println(statusConfig.getIsEditable());
					existingItem.setIsEditable(statusConfig.getIsEditable());
					existingItem.setIsAuthorized(statusConfig.getIsAuthorized());

					if (!existingAmount.equals(updatedItem.getAmount())
							&& !existingRestockingFee.equals(updatedItem.getReStockingAmount())
							&& updatedItem.getAmount() != null && updatedItem.getReStockingAmount() != null) {
						if (statusConfig.getIsAuthorized() || statusConfig.getIsRecieved()) {
							sendRestockingFeeToERP(rmaNo);
							sendAmountToErp(rmaNo, existingItem);
						}
					}

				}

				String auditLogDescription = "";
				String auditLogStatus = "";
				String auditLogTitle = "";
				auditLogDescription = "Item - " + existingItem.getItemName() + " has been updated from "
						+ existingStatus + " to " + updatedItem.getStatus() + " by " + updateBy + ".";
				auditLogStatus = "Line Items";

				auditLogServiceImpl.setAuditLog(auditLogDescription, auditLogTitle, auditLogStatus, rmaNo, updateBy,
						updatedItem.getStatus());
				returnOrderItemRepository.save(existingItem);

				// String recipient = existingItem.getReturnOrder().getCustomer().getEmail();
				String recipient = PortalConstants.EMAIL_RECIPIENT;

				// Handle Status Configurations

				ReturnOrder returnOrderEntity = returnOrderRepository.findByRmaOrderNo(rmaNo).get();
				List<ReturnOrderItem> returnOrderItems = returnOrderItemRepository
						.findByReturnOrderIdAndIsActive(returnOrderEntity.getId(), true);

				int min = 1000;
				for (ReturnOrderItem returnOrderItem : returnOrderItems) {

					StatusConfig statusConfig = statusConfigRepository.findBystatuslabl(returnOrderItem.getStatus())
							.get(0);
					if (statusConfig.getPriority() < min) {
						min = statusConfig.getPriority();

					}

				}

				String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

				MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

				StatusConfig statusConfig = statusConfigRepository.findByPriority(min).get(0);
				String existingHeaderStatus = returnOrderEntity.getStatus();
				returnOrderEntity.setStatus(statusConfig.getStatusMap());
				returnOrderEntity.setIsEditable(statusConfig.getIsEditable());
				returnOrderEntity.setIsAuthorized(statusConfig.getIsAuthorized());

				if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {
						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy + ".";
						String title = "Return Order";
						String status = "RMA Header";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

				} else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.AUTHORIZED)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy + ".;"
								+ "Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

					// Save in ERP
					String apiUrl = masterTenant.getSubdomain() + "/api/sales/orders/"
							+ returnOrderEntity.getRmaOrderNo() + "/approve";
					try {
						// Your existing HTTP request code here
						CloseableHttpClient httpClient = HttpClients.custom()
								.setSSLContext(
										SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

						HttpPut request = new HttpPut(apiUrl);
						try {
							String token = p21TokenServiceImpl.findToken(masterTenant);
							request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
							request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try (CloseableHttpResponse response = httpClient.execute(request)) {
							String responseBody = EntityUtils.toString(response.getEntity());
							System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
							System.out.println("Response Body: " + responseBody);
						}
					} catch (IOException | GeneralSecurityException e) {
						e.printStackTrace();
					}

					// email
					String subject = PortalConstants.RMAStatus + returnOrderServiceImpl.getRmaaQualifier() + " "
							+ returnOrderEntity.getRmaOrderNo();
					String template = emailTemplateRenderer.getRMA_AUTHORIZED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("RMA_NO", returnOrderEntity.getRmaOrderNo());
					map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
					map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
					map.put("CLIENT_PHONE",
							String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
					try {
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}

					// Updating Restocking fee to the ERP
//					sendRestockingFeeToERP(rmaNo);
//
//					String db_name = "";
//					String domain[] = httpServletRequest.getHeader("host").split("\\.");
//					for (String str : domain) {
//						if (str.equals("gocontinuum")) {
//							break;
//						}
//						db_name += str + ".";
//					}
//					if (!db_name.equals("pace.dev.") && !db_name.equals("pace.")) {
//						sendRestockingFeeToERP(rmaNo);
//					}
//
//					List<EditableConfig> findAll = editableConfigRepository.findAll();
//					EditableConfig editableConfig = findAll.get(0);
//					if (editableConfig.isAmountAddition() == true) {
//						Optional<ReturnOrder> findByRmaOrderNo1 = returnOrderRepository.findByRmaOrderNo(rmaNo);
//						ReturnOrder returnOrder1 = findByRmaOrderNo1.get();
//						logger.info("Updating amount to REP");
//						// Updating Amount to the ERP
//						sendAmountToErp(rmaNo, returnOrder1.getReturnOrderItem());
//					}

				} else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.RECIEVED)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy + ".;"
								+ "Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

					// Save in ERP
					String apiUrl = masterTenant.getSubdomain() + "/api/sales/orders/"
							+ returnOrderEntity.getRmaOrderNo() + "/approve";

					try {
						// Your existing HTTP request code here
						CloseableHttpClient httpClient = HttpClients.custom()
								.setSSLContext(
										SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

						HttpPut request = new HttpPut(apiUrl);
						try {
							String token = p21TokenServiceImpl.findToken(masterTenant);
							request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
							request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try (CloseableHttpResponse response = httpClient.execute(request)) {
							String responseBody = EntityUtils.toString(response.getEntity());
							System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
							System.out.println("Response Body: " + responseBody);
						}
					} catch (IOException | GeneralSecurityException e) {
						e.printStackTrace();
					}

					// email
					String subject = PortalConstants.RMAStatus + returnOrderServiceImpl.getRmaaQualifier() + " "
							+ returnOrderEntity.getRmaOrderNo();
					String template = emailTemplateRenderer.getRMA_AUTHORIZED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("RMA_NO", returnOrderEntity.getRmaOrderNo());
					map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
					map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
					map.put("CLIENT_PHONE",
							String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
					try {
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}

//					sendRestockingFeeToERP(rmaNo);
					try {
						String recieptNumber = processRMAAndGetReceiptNumber(Integer.parseInt(rmaNo));
						logger.info("Reciept : " + recieptNumber);
						auditLog.setTitle("Inbox");
						auditLog.setDescription(recieptNumber);
						auditLog.setHighlight("");
						auditLog.setStatus("RMA Header");
						auditLog.setRmaNo(rmaNo);
						auditLog.setUserName(updateBy);
						auditLogRepository.save(auditLog);

					} catch (Exception e) {
						e.printStackTrace();
						auditLog.setTitle("Inbox");
						auditLog.setDescription("RMA Reciept not created");
						auditLog.setHighlight("");
						auditLog.setStatus("RMA Header");
						auditLog.setRmaNo(rmaNo);
						auditLog.setUserName(updateBy);
						auditLogRepository.save(auditLog);
					}

				} else if (statusConfig.getStatusMap()
						.equalsIgnoreCase(PortalConstants.REQUIRES_MORE_CUSTOMER_INFORMATION)) {

					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy + ".;"
								+ " Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

					if (!PortalConstants.REQUIRES_MORE_CUSTOMER_INFORMATION.equals(returnOrder.getStatus())) {
						// apply email functionality.
						String subject = "Action Required! Return: " + returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " Requires more information";
						String template = emailTemplateRenderer.getREQ_MORE_CUST_INFO();
						HashMap<String, String> map = new HashMap<>();
						map.put("RMA_NO", returnOrderEntity.getRmaOrderNo());
						map.put("cust_name", returnOrderEntity.getCustomer().getDisplayName());
						map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
						map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
						map.put("CLIENT_PHONE",
								String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));

						// Database name fetching
						String db_name = "";
						String domain[] = httpServletRequest.getHeader("host").split("\\.");
						for (String str : domain) {
							if (str.equals("gocontinuum")) {
								break;
							}
							db_name += str + ".";
						}
						map.put("SUB_DOMAIN", db_name);
						try {
							emailSender.sendEmail(recipient, template, subject, map);
						} catch (MessagingException e) {
							e.printStackTrace();
						}

					}
				} else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.RMA_CANCLED)) {

					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy + "."
								+ ";Email has been sent to" + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String status = "RMA Header";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

//					apply email functionality.
					String subject = PortalConstants.RMAStatus;
					String template = emailTemplateRenderer.getDENIED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
					map.put("RMA_NO", returnOrderEntity.getRmaOrderNo());
					map.put("CUST_NAME", returnOrderEntity.getCustomer().getDisplayName());
					map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
					map.put("CLIENT_PHONE",
							String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
					try {
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				} else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.RMA_DENIED)) {

					if (!statusConfig.getStatusMap().equals(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated from " + existingHeaderStatus
								+ " to " + "'" + returnOrderEntity.getStatus() + "' by " + updateBy
								+ ".; Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy,
								returnOrderEntity.getStatus());
					}

					// save in ERP while rma denied
					String apiUrl = masterTenant.getSubdomain() + "/uiserver0/api/v2/transaction";
					String xmlData = "<TransactionSet xmlns=\"http://schemas.datacontract.org/2004/07/P21.Transactions.Model.V2\">\r\n"
							+ "    <IgnoreDisabled>true</IgnoreDisabled>\r\n" + "    <Name>RMA</Name>\r\n"
							+ "    <Transactions>\r\n" + "        <Transaction>\r\n" + "            <DataElements>\r\n"
							+ "                <DataElement>\r\n"
							+ "                    <Keys xmlns:a=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\r\n"
							+ "                    <Name>TABPAGE_1.order</Name>\r\n" + "                    <Rows>\r\n"
							+ "                        <Row>\r\n" + "                            <Edits>\r\n"
							+ "								<Edit>\r\n"
							+ "                                            <Name>order_no</Name>\r\n"
							+ "                                            <Value>" + returnOrderEntity.getRmaOrderNo()
							+ "</Value>\r\n" + "                                        </Edit>\r\n"
							+ "                                        <Edit>\r\n"
							+ "                                            <Name>cancel_flag</Name>\r\n"
							+ "                                            <Value>Y</Value>\r\n"
							+ "                                        </Edit>      \r\n"
							+ "                            </Edits>\r\n"
							+ "                            <RelativeDateEdits/>\r\n"
							+ "                        </Row>\r\n" + "                    </Rows>\r\n"
							+ "                    <Type>Form</Type>\r\n" + "                </DataElement>     \r\n"
							+ "            </DataElements>\r\n" + "        </Transaction>\r\n"
							+ "    </Transactions>\r\n" + "</TransactionSet>";

					HttpHeaders headers = new HttpHeaders();
					try {
						headers.setContentType(MediaType.APPLICATION_XML);
						headers.setBearerAuth(p21TokenServiceImpl.findToken(masterTenant));
					} catch (Exception e) {
						e.printStackTrace();
					}
					RestTemplate restTemplate = new RestTemplate();
					try {
						ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST,
								new HttpEntity<>(xmlData, headers), String.class);
						if (response.getStatusCode().is2xxSuccessful()) {
							System.out.println("Resource updated successfully.");
						} else {
							System.out.println("There was an error while updating the resource.");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("An exception occurred while making the API request.");
					}

//					apply email functionality.
					String subject = PortalConstants.RMAStatus + "" + returnOrderServiceImpl.getRmaaQualifier() + " "
							+ returnOrderEntity.getRmaOrderNo();
					String template = emailTemplateRenderer.getDENIED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
					map.put("RMA_NO", returnOrderEntity.getRmaOrderNo());
					map.put("CUST_NAME", returnOrderEntity.getCustomer().getDisplayName());
					map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
					map.put("CLIENT_PHONE",
							String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
					try {
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}

				returnOrderRepository.save(returnOrderEntity);

				// update customer to put tracking code.
				String subject = "Return: " + existingItem.getItemName() + " is Ready and Awaiting Transit";
				HashMap<String, String> map1 = new HashMap<>();
				map1.put("RMA_NO", rmaNo);
				map1.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());

				map1.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
				map1.put("CLIENT_PHONE",
						String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));

				String template1 = emailTemplateRenderer.getEMAIL_LINE_ITEM_STATUS_IN_TRANSIT();

				if ("Authorized Awaiting Transit".equals(updatedItem.getStatus())) {
					try {
//					sendEmail1(recipient, updatedItem.getStatus());
//					emailSender.sendEmailToVender(recipient, updatedItem.getStatus());
						emailSender.sendEmail(recipient, template1, subject, map1);

					} catch (MessagingException e) {
						e.printStackTrace();
					}
				}
			}

			return "List Item Details Updated Successfully.";
		} else {
			throw new EntityNotFoundException("ReturnOrderItem with ID " + id + " not found");
		}

	}

	@Override
	public String updateNote(Long lineItemId, Long assignToId, String rmaNo, String updateBy, Long assignToRole,
			String contactEmail, ReturnOrderItemDTO updateNote) {
		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(lineItemId);
		Optional<Customer> optionalCustomer = customerRepository.findById(assignToId);
		AuditLog auditLog = new AuditLog();
		if (optionalItem.isPresent()) {
			ReturnOrderItem existingItem = optionalItem.get();
			if (optionalCustomer.isPresent() && assignToRole == 4) {
				Optional<User> optionalUser = userRepository.findByCustomerId(optionalCustomer.get().getId());
				User user = optionalUser.get();

				if (updateNote.getFollowUpDate() == null) {
					existingItem.setFollowUpDate(null);
				} else {
					existingItem.setFollowUpDate(updateNote.getFollowUpDate());
				}
				existingItem.setNote(updateNote.getNote());
				existingItem.setVendorMessage(updateNote.getNote());
				existingItem.setUser(user);
				returnOrderItemRepository.save(existingItem);

				String formattedDate = "";
				if (updateNote.getFollowUpDate() != null) {
					Date followUpDate = updateNote.getFollowUpDate();
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd yyyy");
					formattedDate = simpleDateFormat.format(followUpDate);
				} else {
					formattedDate = null;
				}
				auditLog.setDescription(updateBy + " has reassigned note to " + user.getFirstName() + " "
						+ user.getLastName() + " of item - " + existingItem.getItemName()
						+ ". Please review the details and take necessary action.;"
						+ "Vendor Message added and Email has been sent to the " + contactEmail);

				String subject = PortalConstants.NOTE_STATUS_CUSTOMER + returnOrderServiceImpl.getRmaaQualifier() + " "
						+ rmaNo;
				String template2 = emailTemplateRenderer.getVENDER_LINE_ITEM_STATUS_CUSTOMER();
				HashMap<String, String> map = new HashMap<>();

				map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
				map.put("RMA_NO", rmaNo);
				map.put("note", updateNote.getNote());
				map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
				map.put("CLIENT_PHONE",
						String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
				try {
					emailSender.sendEmail(recipient, template2, subject, map);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(updateNote.getNote());
				returnRoom.setAssignTo(user);
				returnRoom.setFollowUpDate(updateNote.getFollowUpDate() != null ? updateNote.getFollowUpDate() : null);
				returnRoom.setStatus(updateNote.getStatus());
				returnRoom.setReturnOrderItem(existingItem);
				returnRoomRepository.save(returnRoom);

			} else {
				Optional<User> optionalUser = userRepository.findById(assignToId);
				User user = optionalUser.get();
				existingItem.setNote(updateNote.getNote());
				existingItem.setUser(user);
				returnOrderItemRepository.save(existingItem);
				auditLog.setDescription(updateBy + " has reassigned note to " + user.getFirstName() + " "
						+ user.getLastName() + " of item - " + existingItem.getItemName()
						+ ". Please review the details and take necessary action.");
				String subject = PortalConstants.NOTE_STATUS + returnOrderServiceImpl.getRmaaQualifier() + " " + rmaNo;
				String template2 = emailTemplateRenderer.getRETURN_PROCESSOR_NOTE();
				HashMap<String, String> map = new HashMap<>();

				map.put("RMA_QUALIFIER", returnOrderServiceImpl.getRmaaQualifier());
				map.put("RMA_NO", rmaNo);
				map.put("ITEM_NAME", existingItem.getItemName());
				map.put("ASSIGNED_TO", user.getFullName());
				map.put("CURRENT_STATUS", updateNote.getStatus());
				map.put("ASSIGNED_BY", updateBy);
				map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
				map.put("CLIENT_PHONE",
						String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
//				Thu Dec 14 05:30:00 IST
				if (updateNote.getFollowUpDate() != null) {
					SimpleDateFormat outputFormat = new SimpleDateFormat("EEE MMM dd yyyy", Locale.ENGLISH);
					String outputDateString = outputFormat.format(updateNote.getFollowUpDate());
					map.put("FOLLOW_UP_DATE", " Please check and follow up on before " + outputDateString);
				} else {
					map.put("FOLLOW_UP_DATE", "");
				}

				try {
					emailSender.sendEmail(recipient, template2, subject, map);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(updateNote.getNote());
				returnRoom.setAssignTo(user);
				returnRoom.setFollowUpDate(updateNote.getFollowUpDate() != null ? updateNote.getFollowUpDate() : null);
				returnRoom.setStatus(updateNote.getStatus());
				returnRoom.setReturnOrderItem(existingItem);
				returnRoomRepository.save(returnRoom);
			}

			auditLog.setTitle("Update Activity");
			auditLog.setHighlight("reassigned note");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);

			return "Updated Note Details and capture in return room and audit log";
		} else {
			return "Not found";
		}
	}

	@Override
	public String updateShipTo(Long rtnOrdId, String rmaNo, String updateBy, OrderAddress orderAddress) {

		Optional<ReturnOrderItem> optionalReturnOrder = returnOrderItemRepository.findById(rtnOrdId);
		StringBuilder stringBuilder = new StringBuilder();
		if (optionalReturnOrder.isPresent()) {
			ReturnOrderItem returnOrderItem = optionalReturnOrder.get();
			returnOrderItem.setShipTo(orderAddress);

			returnOrderItemRepository.save(returnOrderItem);

			if (!orderAddress.getReturnLocNote().isEmpty()) {
				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(returnOrderItem.getShipTo().getReturnLocNote());
				returnRoom.setStatus(returnOrderItem.getStatus());
				returnRoom.setReturnOrderItem(returnOrderItem);
				returnRoomRepository.save(returnRoom);
			}

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription("Shipping Information has been updated of item - " + returnOrderItem.getItemName()
					+ " by " + updateBy + ".");
			auditLog.setHighlight("Shipping Information");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);

			return "Shipping info update";
		} else {
			return "not found";
		}
	}

	@Override
	public String updateRestockingFee(Long id, String rmaNo, String updateBy, BigDecimal reStockingAmount,
			ReturnOrderItemDTO returnOrderItemDTO) {
		Optional<ReturnOrderItem> returnorderitem = returnOrderItemRepository.findById(id);
		if (returnorderitem.isPresent()) {
			ReturnOrderItem returnOrderItem = returnorderitem.get();
			String existingStatus = returnOrderItem.getStatus();

			BigDecimal preRestocking = returnOrderItem.getReStockingAmount();
			if (preRestocking == null) {
				preRestocking = BigDecimal.valueOf(0);
			}
			preRestocking = preRestocking.setScale(0, BigDecimal.ROUND_DOWN);
			if (returnOrderItem.getAmount() == null) {
				returnOrderItem.setAmount(BigDecimal.valueOf(0));
			}

			BigDecimal newReturnAmoun = returnOrderItem.getAmount().subtract(reStockingAmount);

			returnOrderItem.setReStockingAmount(reStockingAmount);
			returnOrderItem.setReturnAmount(newReturnAmoun);

			if (returnOrderItemDTO.getNotes() != null) {
				returnOrderItem.setNotes(returnOrderItemDTO.getNotes());
			}

			// roi.setNotes(returnOrderItemDTO.getNotes());

			returnOrderItemRepository.save(returnOrderItem);

			ReturnRoom returnRoom = new ReturnRoom();
			returnRoom.setName(updateBy);
			returnRoom.setMessage(returnOrderItemDTO.getNotes());
			returnRoom.setReturnOrderItem(returnOrderItem);
			returnRoom.setAssignTo(null);
			returnRoomRepository.save(returnRoom);

			if (existingStatus.equalsIgnoreCase(PortalConstants.AUTHORIZED_AWAITING_TRANSIT)
					|| existingStatus.equalsIgnoreCase(PortalConstants.AUTHORIZED_IN_TRANSIT)
					|| existingStatus.equalsIgnoreCase(PortalConstants.RECIEVED)) {
				sendRestockingFeeToERP(rmaNo);
			}

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription(
					updateBy + " has updated the restocking fee of item - " + returnOrderItem.getItemName() + " from $"
							+ preRestocking + " to $" + returnOrderItem.getReStockingAmount() + ".");
			auditLog.setHighlight("restocking fee");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);

		}
		return "Restocking fee and return amount updated successfully";
	}

	public void sendRestockingFeeToERP(String rmaNo) {
		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(rmaNo);
		if (findByRmaOrderNo.isPresent()) {
			Double totalRestocking = 0d;
			ReturnOrder returnOrder = findByRmaOrderNo.get();
			List<ReturnOrderItem> returnOrderItems = returnOrder.getReturnOrderItem();
			for (ReturnOrderItem returnOrderItem : returnOrderItems) {
				if (returnOrderItem.getReStockingAmount() != null) {
					if (!returnOrderItem.getStatus().equals(PortalConstants.RMA_CANCLED)
							&& !returnOrderItem.getStatus().equals(PortalConstants.RMA_DENIED)) {
						totalRestocking += returnOrderItem.getReStockingAmount().doubleValue();
					}

				}
			}
			String rmaNumber = returnOrder.getRmaOrderNo();
//			Integer poNumber = Integer.parseInt(returnOrder.getPONumber());
			try {
				p21UpdateRMAService.updateRMARestocking(rmaNumber, totalRestocking);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAmountToErp(String rmaNo, ReturnOrderItem returnOrderItem) {
		try {
			p21UpdateRMAService.updateAmount(rmaNo, returnOrderItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<StatusConfig> getAllStatus() {
		return statusConfigRepository.findAll();

	}

	@Override
	public List<QuestionConfig> getQuestions() {
		return questionConfigRepository.findAll();
	}

	@Override
	public Map<String, Object> deleteItem(ReturnOrderItem orderItem, String updateBy, String rmaNo) throws Exception {

		Optional<ReturnOrderItem> returnOrderItem = returnOrderItemRepository.findById(orderItem.getId());
		Map<String, Object> jsonResponse = new HashMap<>();
		ReturnOrderItem item = returnOrderItem.get();
		if (item != null) {
			item.setIsActive(false);
			item.setDeleteNote(orderItem.getDeleteNote());
			returnOrderItemRepository.save(item);

			ReturnRoom returnRoom = new ReturnRoom();
			returnRoom.setName(updateBy);
			returnRoom.setMessage(orderItem.getDeleteNote());
			returnRoom.setReturnOrderItem(item);
			returnRoom.setAssignTo(null);
			returnRoomRepository.save(returnRoom);

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription("Item- " + item.getItemName() + " has been deleted by " + updateBy + ".;"
					+ "Note : " + orderItem.getDeleteNote() + ".");
			auditLog.setHighlight("deleted");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);

			ReturnOrder returnOrderEntity = returnOrderRepository.findByRmaOrderNo(rmaNo).get();
			List<ReturnOrderItem> returnOrderItems = returnOrderItemRepository
					.findByReturnOrderIdAndIsActive(returnOrderEntity.getId(), true);

			if (!returnOrderItems.isEmpty()) {

				int min = 1000;
				for (ReturnOrderItem returnOrderItemEntity : returnOrderItems) {

					StatusConfig statusConfig = statusConfigRepository
							.findBystatuslabl(returnOrderItemEntity.getStatus()).get(0);
					if (statusConfig.getPriority() < min) {
						min = statusConfig.getPriority();

					}

				}

				StatusConfig statusConfig = statusConfigRepository.findByPriority(min).get(0);
				String existingHeaderStatus = returnOrderEntity.getStatus();
				returnOrderEntity.setStatus(statusConfig.getStatusMap());
				returnOrderEntity.setIsEditable(statusConfig.getIsEditable());
				returnOrderEntity.setIsAuthorized(statusConfig.getIsAuthorized());

				returnOrderRepository.save(returnOrderEntity);
			} else {

				StatusConfig statusConfig = statusConfigRepository.findByPriority(25).get(0);
				String existingHeaderStatus = returnOrderEntity.getStatus();
				returnOrderEntity.setStatus(statusConfig.getStatusMap());
				returnOrderEntity.setIsEditable(statusConfig.getIsEditable());
				returnOrderEntity.setIsAuthorized(statusConfig.getIsAuthorized());

				returnOrderRepository.save(returnOrderEntity);

			}

			// updateReturnOrderItem(Long id, String rmaNo, updateBy, orderItem);

			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Item Deleted");

			return jsonResponse;
		}
		jsonResponse.put("status", "error");
		jsonResponse.put("message", "Item Not found");
		return jsonResponse;
	}

	@Override
	public Map<String, Object> addItem(List<ReturnOrderItemDTO> returnOrderItemDTOList, String updateBy, String rmaNo) {
		Optional<ReturnOrder> optionalReturnOrder = returnOrderRepository.findByRmaOrderNo(rmaNo);
		Map<String, Object> jsonResponse = new HashMap<>();
		if (optionalReturnOrder.isPresent()) {
			ReturnOrder returnOrder = optionalReturnOrder.get();
			List<ReturnOrderItem> returnOrderItems = returnOrder.getReturnOrderItem();
			Set<String> existingItemNames = new HashSet<>();

			for (ReturnOrderItem existingItem : returnOrderItems) {
				existingItemNames.add(existingItem.getItemName());
			}

			for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderItemDTOList) {
				String itemName = returnOrderItemDTO.getItemName();

				// Check if the item already exists for the given RMA
				if (existingItemNames.contains(itemName) && Boolean.TRUE.equals(returnOrderItemDTO.getIsActive())) {
					// Handle the case where the item already exists
					jsonResponse.put("status", "error");
					jsonResponse.put("message", "Item Already Exists");
					return jsonResponse;
				}

				// Add the item name to the set to prevent duplicates
				existingItemNames.add(itemName);

				// Rest of your code
				returnOrderItemDTO.setQuanity(returnOrderItemDTO.getQuanity());
				returnOrderItemDTO.setItemName(itemName);
				returnOrderItemDTO.setItemDesc(returnOrderItemDTO.getItemDesc());
				returnOrderItemDTO.setStatus(returnOrderItemDTO.getStatus());
				returnOrderItemDTO.setReasonCode(returnOrderItemDTO.getReasonCode());
				returnOrderItemDTO.setIsEditable(true);
				returnOrderItemDTO.setIsAuthorized(false);
				returnOrderItemDTO.setIsActive(true);

				ReturnOrderItem returnOrderItem = returnOrderItemMapper
						.returnOrderItemDTOToReturnOrderItem(returnOrderItemDTO);

				try {
					returnOrderItemRepository.save(returnOrderItem);
					returnOrderItemRepository.updateReturnOrder(returnOrderItem.getId(), returnOrder.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}

				ReturnOrder returnOrderEntity = returnOrderRepository.findByRmaOrderNo(rmaNo).get();
				List<ReturnOrderItem> returnOrderItemList = returnOrderItemRepository
						.findByReturnOrderIdAndIsActive(returnOrderEntity.getId(), true);

				if (!returnOrderItemList.isEmpty()) {

					int min = 1000;
					for (ReturnOrderItem returnOrderItemEntity : returnOrderItemList) {

						StatusConfig statusConfig = statusConfigRepository
								.findBystatuslabl(returnOrderItemEntity.getStatus()).get(0);
						if (statusConfig.getPriority() < min) {
							min = statusConfig.getPriority();

						}

					}

					StatusConfig statusConfig = statusConfigRepository.findByPriority(min).get(0);
					String existingHeaderStatus = returnOrderEntity.getStatus();
					returnOrderEntity.setStatus(statusConfig.getStatusMap());
					returnOrderEntity.setIsEditable(statusConfig.getIsEditable());
					returnOrderEntity.setIsAuthorized(statusConfig.getIsAuthorized());

					returnOrderRepository.save(returnOrderEntity);
				}

				// Capture in audit logs
				List<String> updates = new ArrayList<>();
				String description = "Item- " + itemName + " has been added by " + updateBy + ".;" + "Item- " + itemName
						+ " has been updated to " + returnOrderItemList.get(0).getStatus() + ".;";

				String title = "Update Activity";
				String status = "List Items";
				String highlight = "added";
				auditLogServiceImpl.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
			}

			jsonResponse.put("status", "success");
			jsonResponse.put("message", "Item(s) added successfully");
			return jsonResponse;
		}

		jsonResponse.put("status", "error");
		jsonResponse.put("message", "Return Order Not Found");
		return jsonResponse;
	}

	public String processRMAAndGetReceiptNumber(int rmaNo) throws Exception {

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);

		String rmaDetailsUrl = masterTenant.getSubdomain() + rmaGetEndPoint + "/get";
		String rmaReceiptUrl = masterTenant.getSubdomain() + rmaGetEndPoint;
		String accessToken = "Bearer: " + p21TokenServiceImpl.findToken(masterTenant);

		logger.info("First URL" + rmaDetailsUrl);
		logger.info("Second URL" + rmaReceiptUrl);
		logger.info("TOKEN" + accessToken);

		try (CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build()) {

			HttpPost httpPost = new HttpPost(rmaDetailsUrl);
			httpPost.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
			httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
			httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			String requestBody = constructFirstApiRequestBody(rmaNo);

			logger.info("This is request Body for first API" + requestBody);

			httpPost.setEntity(new StringEntity(requestBody));

			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
				String responseBody = EntityUtils.toString(response.getEntity());

				logger.info("This is response Body for first API" + responseBody);

				JsonNode rootNode = objectMapper.readTree(responseBody);

				String orderNo = rootNode.path("Transactions").get(0).path("DataElements").get(0).path("Rows").get(0)
						.path("Edits").get(0).path("Value").asText();
				String rmaExpirationDate = rootNode.path("Transactions").get(0).path("DataElements").get(0).path("Rows")
						.get(0).path("Edits").get(11).path("Value").asText();
				String salesLocId = rootNode.path("Transactions").get(0).path("DataElements").get(0).path("Rows").get(0)
						.path("Edits").get(3).path("Value").asText();

				JsonNode itemsNode = rootNode.path("Transactions").get(0).path("DataElements").get(42).path("Rows");
				List<String> itemIdsList = new ArrayList<>();
				List<String> itemQuantitiesList = new ArrayList<>();
				for (JsonNode item : itemsNode) {
					// Picking up item id/item ids from rma

					String itemId = item.path("Edits").get(0).path("Value").asText();
					itemIdsList.add(itemId);
					System.out.println("Item ID: " + itemId);

					// Picking up rma quantity from Unit_Quantity field

					String itemQuantity = item.path("Edits").get(3).path("Value").asText();
					itemQuantitiesList.add(itemQuantity);
					System.out.println("Item Quantity: " + itemQuantity);
				}

				logger.info("This is orderNo: " + orderNo);
				logger.info("This is rmaExpirationDate: " + rmaExpirationDate);
				logger.info("This is orderNo: " + salesLocId);
				logger.info("List of item ids: " + itemIdsList);

				String secondApiRequestBody = constructSecondApiRequestBody(orderNo, rmaExpirationDate, salesLocId,
						itemIdsList, itemQuantitiesList);

				logger.info("Second API REQUEST BODY: " + secondApiRequestBody);

				HttpPost httpPost1 = new HttpPost(rmaReceiptUrl);
				httpPost1.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
				httpPost1.setHeader(HttpHeaders.ACCEPT, "application/json");
				httpPost1.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				httpPost1.setEntity(new StringEntity(secondApiRequestBody));

				try (CloseableHttpResponse response1 = httpClient.execute(httpPost1)) {
					String responseBody1 = EntityUtils.toString(response1.getEntity());

					logger.info("This is second response: " + responseBody1);
					JsonNode responseNode = objectMapper.readTree(responseBody1);

					int succeededCount = responseNode.path("Summary").path("Succeeded").asInt();
					logger.info("This is where succedded should be 1" + succeededCount);

					if (succeededCount > 0) {

						String receiptNumber = responseNode.path("Results").path("Transactions").get(0)
								.path("DataElements").get(0).path("Rows").get(0).path("Edits").get(0).path("Value")
								.asText();

						logger.info("This is receipt Number" + receiptNumber);

						return "RMA Receipt created successfully. Receipt Number: " + receiptNumber;
					} else {
						return "RMA Receipt not generated. No successful transactions.";
					}
				}
			} catch (Exception e) {
				logger.info("First api failed.");
				e.printStackTrace();
				logger.info("HOLD");
			}
		}
		return null;
	}

	private String constructSecondApiRequestBody(String orderNo, String rmaExpirationDate, String salesLocId,
			List<String> itemIdsList, List<String> itemQuantitiesList) {

		ObjectNode rootNode = objectMapper.createObjectNode();
		rootNode.put("IgnoreDisabled", true);
		rootNode.put("Name", "RMAReceipt");
		rootNode.put("UseCodeValues", false);

		ArrayNode transactionsArray = rootNode.putArray("Transactions");
		ObjectNode transactionObject = transactionsArray.addObject();
		transactionObject.put("Status", "New");

		ArrayNode dataElementsArray = transactionObject.putArray("DataElements");

		ObjectNode headerForm = dataElementsArray.addObject();
		headerForm.put("Name", "TABPAGE_1.header");
		headerForm.put("Type", "Form");

		ArrayNode headerRowsArray = headerForm.putArray("Rows");
		ObjectNode headerRow = headerRowsArray.addObject();
		ArrayNode headerEditsArray = headerRow.putArray("Edits");

		addEdit(headerEditsArray, "c_location_id", salesLocId);
		addEdit(headerEditsArray, "order_no", orderNo);
		addEdit(headerEditsArray, "confirm_receipt", "OFF");
		addEdit(headerEditsArray, "front_counter_rma", "ON");
		addEdit(headerEditsArray, "print_itempackage_labels", "OFF");
		addEdit(headerEditsArray, "rma_expiration_date", rmaExpirationDate);
		addEdit(headerEditsArray, "invoice_batch_number", "");
		addEdit(headerEditsArray, "invoice_edi", "OFF");
		addEdit(headerEditsArray, "c_invoice_date", "");
		addEdit(headerEditsArray, "c_inv_period", "");
		addEdit(headerEditsArray, "c_inv_yr_for_period", "");

		ObjectNode itemsList = dataElementsArray.addObject();
		itemsList.put("Name", "TABPAGE_17.items");
		itemsList.put("Type", "List");

		ArrayNode itemsRowsArray = itemsList.putArray("Rows");
		for (int i = 0; i < itemIdsList.size(); i++) {
			String itemId = itemIdsList.get(i);
			String itemQuantity = itemQuantitiesList.get(i);

			ObjectNode itemRow = itemsRowsArray.addObject();
			ArrayNode itemEditsArray = itemRow.putArray("Edits");

			addEdit(itemEditsArray, "oe_order_item_id", itemId);
			addEdit(itemEditsArray, "c_rma_qty_received", itemQuantity);
			addEdit(itemEditsArray, "c_rma_qty_to_return_to_stock", itemQuantity);
			addEdit(itemEditsArray, "c_complete", "OFF");
		}
		return rootNode.toPrettyString();
	}

	// Helper methods for making Receipt

	private String constructFirstApiRequestBody(int rmaNo) {
		return "{ \"ServiceName\":\"RMA\", " + "\"TransactionStates\":[{ \"DataElementName\":\"TABPAGE_1.order\", "
				+ "\"Keys\":[{ \"Name\":\"order_no\", \"Value\":" + rmaNo + " }] }], " + "\"UseCodeValues\":true }";
	}

	private void addEdit(ArrayNode editsArray, String name, String value) {
		ObjectNode editObject = editsArray.addObject();
		editObject.put("Name", name);
		editObject.put("Value", value);
	}
}
