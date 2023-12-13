package com.continuum.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
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
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionConfig;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.ReturnRoom;
import com.continuum.tenant.repos.entity.StatusConfig;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.QuestionConfigRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.continuum.tenant.repos.repositories.ReturnRoomRepository;
import com.continuum.tenant.repos.repositories.StatusConfigRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.integration.p21.service.P21UpdateRMAService;
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;

import ch.qos.logback.classic.Logger;

@Service
public class ReturnOrderItemServiceImpl implements ReturnOrderItemService {
	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuditLogRepository auditLogRepository;
	@Autowired
	QuestionConfigRepository questionConfigRepository;

	@Autowired
	ReturnRoomRepository returnRoomRepository;

	@Autowired
	StatusConfigRepository statusConfigRepository;

	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Autowired
	EmailSender emailSender;

	@Autowired
	P21UpdateRMAService p21UpdateRMAService;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

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

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public String updateReturnOrderItem(Long id, String rmaNo, String updateBy, ReturnOrderItemDTO updatedItem) {

		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(id);
		Optional<ReturnOrder> findByRmaOrderNo = returnOrderRepository.findByRmaOrderNo(rmaNo);
		ReturnOrder returnOrder = findByRmaOrderNo.get();

		if (optionalItem.isPresent()) {
			AuditLog auditLog = new AuditLog();
			ReturnOrderItem existingItem = optionalItem.get();
			String previousStatus = existingItem.getStatus();
			String previousRC = existingItem.getReasonCode();
			String previousPD = existingItem.getProblemDesc();
			String TrackingUrl = existingItem.getTrackingUrl();
			String TrackingNumber = existingItem.getTrackingNumber();
			String CourierName = existingItem.getCourierName();

			// Update only the fields that are not null in updatedItem
			if (updatedItem.getProblemDescNote() != null) {
				existingItem.setProblemDescNote(updatedItem.getProblemDescNote());
				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(updatedItem.getProblemDescNote());
				returnRoom.setReturnOrderItem(existingItem);
				returnRoom.setAssignTo(null);
				returnRoomRepository.save(returnRoom);
				returnOrderItemRepository.save(existingItem);

			}

			if (updatedItem.getAmount() != null || updatedItem.getAmountNote() != null) {
				existingItem.setAmount(updatedItem.getAmount());
				existingItem.setAmountNote(updatedItem.getAmountNote());

				ReturnRoom returnRoom = new ReturnRoom();
				returnRoom.setName(updateBy);
				returnRoom.setMessage(updatedItem.getAmountNote());
				returnRoom.setReturnOrderItem(existingItem);
				returnRoom.setAssignTo(null);
				returnRoomRepository.save(returnRoom);
				returnOrderItemRepository.save(existingItem);

				auditLog.setDescription(
						"Amount has been updated of item - " + existingItem.getItemName() + " by " + updateBy + ".");
				auditLog.setHighlight("Amount");
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Line Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
				auditLogRepository.save(auditLog);
				returnOrderItemRepository.save(existingItem);
			}

//			if (updatedItem.getReturnLocNote() != null && updatedItem.getReturnLocRole() != null) {
//				existingItem.setReturnLocNote(updatedItem.getReturnLocNote());
//				existingItem.setReturnLocRole(updatedItem.getReturnLocRole());
//				ReturnRoom returnRoom = new ReturnRoom();
//				returnRoom.setName(updateBy);
//				returnRoom.setMessage(updatedItem.getReturnLocNote());
//				returnRoom.setReturnOrderItem(existingItem);
//				returnRoom.setAssignTo(null);
//				returnRoomRepository.save(returnRoom);
//
//				auditLog.setDescription(updateBy + " added a new vendor as " + updatedItem.getReturnLocRole()
//						+ " for item - " + existingItem.getItemName() + ".");
//				auditLog.setHighlight("");
//				auditLog.setTitle("Update Activity");
//				auditLog.setStatus("Ordered Items");
//				auditLog.setRmaNo(rmaNo);
//				auditLog.setUserName(updateBy);
//				auditLogRepository.save(auditLog);
//				returnOrderItemRepository.save(existingItem);
//
//			}

			if (updatedItem.getTrackingNumber() != null || updatedItem.getCourierName() != null
					|| updatedItem.getTrackingUrl() != null) {
				existingItem.setTrackingNumber(updatedItem.getTrackingNumber());
				existingItem.setTrackingUrl(updatedItem.getTrackingUrl());
				existingItem.setCourierName(updatedItem.getCourierName());
				auditLog.setDescription("Tracking Detail has been updated of item - " + existingItem.getItemName()
						+ " by " + updateBy + ".");
				auditLog.setHighlight("Tracking Detail");
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Line Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
				auditLogRepository.save(auditLog);
				returnOrderItemRepository.save(existingItem);

			}
			if (updatedItem.getStatus() != null) {
				existingItem.setStatus(updatedItem.getStatus());
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_AWAITING_TRANSIT)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_IN_TRANSIT)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_CANCLED)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_LINE_DENIED)
						|| updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_DENIED)) {
					List<StatusConfig> statusConfigList = statusConfigRepository
							.findBystatuslabl(updatedItem.getStatus());
					StatusConfig statusConfig = statusConfigList.get(0);
					System.err.println(statusConfig.getIsEditable());
					existingItem.setIsEditable(statusConfig.getIsEditable());
					existingItem.setIsAuthorized(statusConfig.getIsAuthorized());
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Under Review")) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Under Review' by " + updateBy + ".");
					auditLog.setHighlight("Under Review");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.REQUIRES_MORE_CUSTOMER_INFORMATION)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Requires more customer  information' by " + updateBy + ".");
					auditLog.setHighlight("Requires more customer  information");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AWAITING_CARRIER_APPROVAL)) {
					auditLog.setDescription(returnOrderServiceImpl.getRmaaQualifier() + " " + rmaNo
							+ " has been updated to 'Under Review';Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Awaiting Carrier approval' by " + updateBy + "");
					auditLog.setHighlight("Under Review");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AWAITING_VENDOR_APPROVAL)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Awaiting Vender approval' by " + updateBy + ".");
					auditLog.setHighlight("Awaiting Vender approval");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_AWAITING_TRANSIT)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Authorized Awaiting Transit' by " + updateBy + ".");
					auditLog.setHighlight("Authorized Awaiting Transit");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.AUTHORIZED_IN_TRANSIT)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'Authorized In Transit' by " + updateBy + ".");
					auditLog.setHighlight("Authorized In Transit'");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_DENIED)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'RMA Denied' by " + updateBy + ".");
					auditLog.setHighlight("RMA line Denied");
				}
				if (updatedItem.getStatus().equalsIgnoreCase(PortalConstants.RMA_CANCLED)) {
					auditLog.setDescription("Item - " + existingItem.getItemName()
							+ " has been assigned to the 'RMA line Cancelled' by " + updateBy + ".");
					auditLog.setHighlight("RMA line Cancelled");
				}
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Line Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
				auditLogRepository.save(auditLog);
				returnOrderItemRepository.save(existingItem);

				// String recipient = existingItem.getReturnOrder().getCustomer().getEmail();
				String recipient = PortalConstants.EMAIL_RECIPIENT;

				// Handle Status Configurations

				ReturnOrder returnOrderEntity = returnOrderRepository.findByRmaOrderNo(rmaNo).get();
				List<ReturnOrderItem> returnOrderItems = returnOrderItemRepository
						.findByReturnOrderId(returnOrderEntity.getId());

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

				returnOrderEntity.setStatus(statusConfig.getStatusMap());
				returnOrderEntity.setIsEditable(statusConfig.getIsEditable());
				returnOrderEntity.setIsAuthorized(statusConfig.getIsAuthorized());

				if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {
						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated to 'Under Review' by "
								+ updateBy + ".";
						String title = "Return Order";
						String highlight = "Under Review";
						String status = "RMA Header";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
					}

				} else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.AUTHORIZED)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated to 'Authorized' by " + updateBy
								+ ". The return is approved. Please proceed with the necessary steps." + "; "
								+ "Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String highlight = "Authorized";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
					}

					// Save in ERP
					String apiUrl = masterTenant.getSubdomain() + "/api/sales/orders/"
							+ returnOrderEntity.getRmaOrderNo() + "/approve";
//					RestTemplate restTemplate = new RestTemplate();
//					HttpHeaders headers = new HttpHeaders();
//					try {
//						headers.setBearerAuth(p21TokenServiceImpl.getToken(masterTenant));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					HttpEntity<String> entity = new HttpEntity<>(headers);
//					ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity,
//							String.class);
//
//					if (response.getStatusCode() == HttpStatus.OK) {
//						System.out.println("Saving Status Approved In ERP.");
//					} else {
//						System.out.println("There was an error while saving status in ERP.");
//					}
					try {
						// Your existing HTTP request code here
						CloseableHttpClient httpClient = HttpClients.custom()
								.setSSLContext(
										SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

						HttpPut request = new HttpPut(apiUrl);
						try {
							String token = p21TokenServiceImpl.getToken(masterTenant);
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
					
					
					sendRestockingFeeToERP(rmaNo);

				}else if (statusConfig.getStatusMap().equalsIgnoreCase(PortalConstants.RECIEVED)) {
					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo() + " has been updated to 'Recieved' by " + updateBy
								+ ". The return is approved. Please proceed with the necessary steps." + "; "
								+ "Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String highlight = "Recieved";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
					}

					// Save in ERP
					String apiUrl = masterTenant.getSubdomain() + "/api/sales/orders/"
							+ returnOrderEntity.getRmaOrderNo() + "/approve";
//					RestTemplate restTemplate = new RestTemplate();
//					HttpHeaders headers = new HttpHeaders();
//					try {
//						headers.setBearerAuth(p21TokenServiceImpl.getToken(masterTenant));
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					HttpEntity<String> entity = new HttpEntity<>(headers);
//					ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity,
//							String.class);
//
//					if (response.getStatusCode() == HttpStatus.OK) {
//						System.out.println("Saving Status Approved In ERP.");
//					} else {
//						System.out.println("There was an error while saving status in ERP.");
//					}
					try {
						// Your existing HTTP request code here
						CloseableHttpClient httpClient = HttpClients.custom()
								.setSSLContext(
										SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
								.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

						HttpPut request = new HttpPut(apiUrl);
						try {
							String token = p21TokenServiceImpl.getToken(masterTenant);
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
					
					
					sendRestockingFeeToERP(rmaNo);

				} else if (statusConfig.getStatusMap()
						.equalsIgnoreCase(PortalConstants.REQUIRES_MORE_CUSTOMER_INFORMATION)) {

					if (!statusConfig.getStatusMap().equalsIgnoreCase(returnOrder.getStatus())) {

						String description = returnOrderServiceImpl.getRmaaQualifier() + " "
								+ returnOrderEntity.getRmaOrderNo()
								+ " has been updated to 'Requires More Customer Information' by " + updateBy
								+ ". Awaiting more information with customer.; Email has been sent to the "
								+ returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String highlight = "Requires More Customer Information";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
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
						
						//Database name fetching
						String db_name = httpServletRequest.getHeader("host").split("\\.")[0]+".dev";
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
								+ returnOrderEntity.getRmaOrderNo() + " has been updated to 'Cancelled' by " + updateBy
								+ "." + ";Email has been sent to" + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String highlight = "Cancelled";
						String status = "RMA Header";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
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
								+ returnOrderEntity.getRmaOrderNo() + " has been updated to 'DENIED' by " + updateBy
								+ ".; Email has been sent to the " + returnOrderEntity.getContact().getContactEmailId();
						String title = "Return Order";
						String highlight = "Denied";
						String status = "Inbox";
						auditLogService.setAuditLog(description, title, status, rmaNo, updateBy, highlight);
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
						headers.setBearerAuth(p21TokenServiceImpl.getToken(masterTenant));
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
	public String updateNote(Long lineItemId, Long assignToId, String rmaNo, String updateBy, String contactEmail,
			ReturnOrderItemDTO updateNote) {
		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(lineItemId);
		Optional<User> optionalUser = userRepository.findById(assignToId);
		if (optionalItem.isPresent() && optionalUser.isPresent()) {
			ReturnOrderItem existingItem = optionalItem.get();
			User user = optionalUser.get();
			user.setUserName(user.getUserName());
			user.setFirstName(user.getFirstName());
			user.setLastName(user.getLastName());
			user.setEmail(user.getEmail());
			user.setRole(user.getRole());
			userRepository.save(user);

			if (updateNote.getFollowUpDate() == null) {
				existingItem.setFollowUpDate(null);
			} else {
				existingItem.setFollowUpDate(updateNote.getFollowUpDate());
			}
			existingItem.setNote(updateNote.getNote());
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
			AuditLog auditLog = new AuditLog();
			if (user.getRole().getId() == 4) {
				existingItem.setVendorMessage(updateNote.getNote());
				returnOrderItemRepository.save(existingItem);

				if (updateBy.equalsIgnoreCase(user.getFirstName() + " " + user.getLastName())) {
					auditLog.setDescription("A note has been assigned to " + user.getFirstName() + " "
							+ user.getLastName() + " of item - " + existingItem.getItemName()
							+ ". Please review the details and take necessary action." + ";"
							+ "Vendor Message added and Email has been sent to the " + contactEmail);
				} else {
					auditLog.setDescription(updateBy + " has reassigned note to " + user.getFirstName() + " "
							+ user.getLastName() + " of item - " + existingItem.getItemName()
							+ ". Please review the details and take necessary action."
							+ "Vendor Message added and Email has been sent to the " + contactEmail);
				}

					String subject = PortalConstants.NOTE_STATUS_CUSTOMER + returnOrderServiceImpl.getRmaaQualifier()
							+ " " + rmaNo;
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
			} else {
				if (updateBy.equalsIgnoreCase(user.getFirstName() + " " + user.getLastName())) {
					auditLog.setDescription("A note has been assigned to " + user.getFirstName() + " "
							+ user.getLastName() + " of item - " + existingItem.getItemName()
							+ ". Please review the details and take necessary action.");
				} else {
					auditLog.setDescription(updateBy + " has reassigned note to " + user.getFirstName() + " "
							+ user.getLastName() + " of item - " + existingItem.getItemName()
							+ ". Please review the details and take necessary action.");
				}
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
			}

			ReturnRoom returnRoom = new ReturnRoom();
			returnRoom.setName(updateBy);
			returnRoom.setMessage(updateNote.getNote());
			returnRoom.setAssignTo(user);
			returnRoom.setFollowUpDate(updateNote.getFollowUpDate() != null ? updateNote.getFollowUpDate() : null);
			returnRoom.setStatus(updateNote.getStatus());
			returnRoom.setReturnOrderItem(existingItem);
			returnRoomRepository.save(returnRoom);

			auditLog.setTitle("Update Activity");
			auditLog.setHighlight("");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);
//			if(existingItem.getUser().getId() != assignToId) {
//				
//			}else {
//				Date followUpDate = updateNote.getFollowUpDate();
//				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd yyyy");
//				String formattedDate = simpleDateFormat.format(followUpDate);
//				String recipient = existingItem.getUser().getEmail();
//				System.out.println(recipient);
//				String template = emailTemplateRenderer.getEMAIL_NOTE_STATUS();
//				
//				HashMap<String, String> map1 = new HashMap<>();
//				map1.put("name", updateBy);
//				map1.put("date", formattedDate);
//				
//				try {
////					emailSender.emailToCustomer(recipient, updateBy, formattedDate);
//					emailSender.sendEmail(recipient, template, subject, map1);
//				} catch (MessagingException e) {
//					e.printStackTrace();
//				}
//			}

			return "Updated Note Details and capture in return room and audit log";

		} else {
			return "Not Found";

		}

	}

	@Override
	public String updateShipTo(Long rtnOrdId, String rmaNo, String updateBy, OrderAddress orderAddress) {

		Optional<ReturnOrderItem> optionalReturnOrder = returnOrderItemRepository.findById(rtnOrdId);
		StringBuilder stringBuilder = new StringBuilder();
		if (optionalReturnOrder.isPresent()) {
			ReturnOrderItem returnOrderItem = optionalReturnOrder.get();

//			if (orderAddress.getFirstName() != null) {
			returnOrderItem.getShipTo().setFirstName(orderAddress.getFirstName());
//				stringBuilder.append("First Name has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getLastName() != null) {
			returnOrderItem.getShipTo().setLastName(orderAddress.getLastName());
//				stringBuilder.append("Last Name has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getAddressType() != null) {
			returnOrderItem.getShipTo().setAddressType(orderAddress.getAddressType());
//				stringBuilder.append("Address Type has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getFax() != null) {
			returnOrderItem.getShipTo().setFax(orderAddress.getFax());
//				stringBuilder.append("Fax has been updated in shipping information of " + returnOrderItem.getItemName() + " by "
//						+ updateBy + ";");
//			}
//			if (orderAddress.getStreet1() != null) {
			returnOrderItem.getShipTo().setStreet1(orderAddress.getStreet1());
//				stringBuilder.append("Street1 has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getStreet2() != null) {
			returnOrderItem.getShipTo().setStreet2(orderAddress.getStreet2());
//				stringBuilder.append("Street2 has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getZipcode() != null) {
			returnOrderItem.getShipTo().setZipcode(orderAddress.getZipcode());
//				stringBuilder.append("Zipcode has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getCity() != null) {
			returnOrderItem.getShipTo().setCity(orderAddress.getCity());
//				stringBuilder.append("City has been updated in shipping information of " + returnOrderItem.getItemName() + " by "
//						+ updateBy + ";");
//			}
//			if (orderAddress.getCountry() != null) {
			returnOrderItem.getShipTo().setCountry(orderAddress.getCountry());
//				stringBuilder.append("Country has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getProvince() != null) {
			returnOrderItem.getShipTo().setProvince(orderAddress.getProvince());
//				stringBuilder.append("Province has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getPhoneNumber() != null) {
			returnOrderItem.getShipTo().setPhoneNumber(orderAddress.getPhoneNumber());
//				stringBuilder.append("Phone Number has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			if (orderAddress.getEmailAddress() != null) {
			returnOrderItem.getShipTo().setEmailAddress(orderAddress.getEmailAddress());
			returnOrderItem.getShipTo().setReturnLocNote(orderAddress.getReturnLocNote());

//				stringBuilder.append("Email Address has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			String res = stringBuilder.toString();

			returnOrderItemRepository.save(returnOrderItem);

			ReturnRoom returnRoom = new ReturnRoom();
			returnRoom.setName(updateBy);
			returnRoom.setMessage(returnOrderItem.getShipTo().getReturnLocNote());
			returnRoom.setStatus(returnOrderItem.getStatus());
			returnRoom.setReturnOrderItem(returnOrderItem);
			returnRoomRepository.save(returnRoom);

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription("Shipping Information has been updated of item - " + returnOrderItem.getItemName()
					+ " by " + updateBy + ".");
			auditLog.setHighlight("");
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

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription(
					updateBy + " has been updated the restocking fee of item - " + returnOrderItem.getItemName()
							+ " from $" + preRestocking + " to $" + returnOrderItem.getReStockingAmount() + ".");
			auditLog.setHighlight("");
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
					totalRestocking += returnOrderItem.getReStockingAmount().doubleValue();
				}
			}
			Integer rmaNumber = Integer.parseInt(returnOrder.getRmaOrderNo());
			Integer poNumber = Integer.parseInt(returnOrder.getPONumber());
			try {
				p21UpdateRMAService.updateRMARestocking(rmaNumber, poNumber, totalRestocking);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public String deleteItem(ReturnOrderItem orderItem, String updateBy, String rmaNo) {
		Optional<ReturnOrderItem> returnOrderItem = returnOrderItemRepository.findById(orderItem.getId());
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
			auditLog.setHighlight("");
			auditLog.setStatus("List Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);

			return "Item Deleted";
		}
		return "Item Not found";
	}
}
