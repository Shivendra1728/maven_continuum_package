package com.continuum.serviceImpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.constants.PortalConstants;
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
import com.di.integration.p21.serviceImpl.P21TokenServiceImpl;

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
	@Override
	public String updateReturnOrderItem(Long id, String rmaNo, String updateBy, ReturnOrderItemDTO updatedItem) {

		
		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(id);

		if (optionalItem.isPresent()) {
			AuditLog auditLog = new AuditLog();
			ReturnOrderItem existingItem = optionalItem.get();
			String previousStatus = existingItem.getStatus();
			String previousRC = existingItem.getReasonCode();
			String previousPD = existingItem.getProblemDesc();
			String TrackingUrl = existingItem.getTrackingUrl();
			Long TrackingNumber = existingItem.getTrackingNumber();
			String CourierName = existingItem.getCourierName();

			// Update only the fields that are not null in updatedItem
			if (updatedItem.getStatus() != null) {
				existingItem.setStatus(updatedItem.getStatus());
				if (updatedItem.getStatus().equalsIgnoreCase("Under Review")) {
					auditLog.setDescription("Item - "+existingItem.getItemName() + " has been assigned to the 'Under Review' by "
							+ updateBy + ".");
					auditLog.setHighlight("Under Review");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Requires more customer  information")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'Requires more customer  information' by " + updateBy + ".");
					auditLog.setHighlight("Requires more customer  information");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Awaiting Vendor approval")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'Awaiting Vendor approval' by " + updateBy + ".");
					auditLog.setHighlight("Awaiting Vendor approval");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Awaiting Carrier approval")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'Awaiting Carrier approval' by " + updateBy + ".");
					auditLog.setHighlight("Awaiting Carrier approval");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Authorized Awaiting Transit")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'Authorized Awaiting Transit' by " + updateBy + ".");
					auditLog.setHighlight("Authorized Awaiting Transit");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("Authorized In Transit")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'Authorized In Transit' by " + updateBy + ".");
					auditLog.setHighlight("Authorized In Transit'");
				}
				if (updatedItem.getStatus().equalsIgnoreCase("RMA line Denied")) {
					auditLog.setDescription("Item - "+existingItem.getItemName()
							+ " has been assigned to the 'RMA line Denied' by " + updateBy + ".");
					auditLog.setHighlight("RMA line Denied");
				}
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Ordered Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
			}

			if (updatedItem.getProblemDesc() != null && updatedItem.getReasonCode()!=null&& !(existingItem.getProblemDesc().equalsIgnoreCase(updatedItem.getProblemDesc())) && !(existingItem.getReasonCode().equalsIgnoreCase(updatedItem.getReasonCode()))) {
				existingItem.setReasonCode(updatedItem.getReasonCode());
				existingItem.setProblemDesc(updatedItem.getProblemDesc());
				auditLog.setDescription("Reason Listing ---> Problem Details  has been updated of item - "+existingItem.getItemName()+" by "+updateBy+".");
				auditLog.setHighlight("");
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Ordered Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
			}
			if (updatedItem.getProblemDesc() != null && (existingItem.getReasonCode().equalsIgnoreCase(updatedItem.getReasonCode())) && !(existingItem.getProblemDesc().equalsIgnoreCase(updatedItem.getProblemDesc()))) {
				existingItem.setProblemDesc(updatedItem.getProblemDesc());
				auditLog.setDescription("Problem Details has been updated of item - "+existingItem.getItemName()+" by "+updateBy+".");
				auditLog.setHighlight("");		
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Ordered Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
			}
			if (updatedItem.getReasonCode() != null && (existingItem.getProblemDesc().equalsIgnoreCase(updatedItem.getProblemDesc())) && !(existingItem.getReasonCode().equalsIgnoreCase(updatedItem.getReasonCode()))) {
				existingItem.setReasonCode(updatedItem.getReasonCode());
				auditLog.setDescription("Reason Listing has been updated of item - "+existingItem.getItemName()+" by "+updateBy+".");
				auditLog.setHighlight("");
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Ordered Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
			}

			if (updatedItem.getTrackingUrl() != null || updatedItem.getTrackingNumber() != null
					|| updatedItem.getCourierName() != null) {
				existingItem.setTrackingUrl(updatedItem.getTrackingUrl());
				existingItem.setTrackingNumber(updatedItem.getTrackingNumber());
				existingItem.setCourierName(updatedItem.getCourierName());
				auditLog.setDescription("Tracking Code has been updated of item - "+existingItem.getItemName()+" by "+updateBy+".");
				auditLog.setHighlight("");
				auditLog.setTitle("Update Activity");
				auditLog.setStatus("Ordered Items");
				auditLog.setRmaNo(rmaNo);
				auditLog.setUserName(updateBy);
			}

			returnOrderItemRepository.save(existingItem);
			auditLogRepository.save(auditLog);
			//String recipient = existingItem.getReturnOrder().getCustomer().getEmail();
			String recipient=PortalConstants.EMAIL_RECIPIENT;

			// Handle Status Configurations
			boolean hasUnderReview = false;
			boolean hasRequiresMoreCustomerInfo = false;
			boolean allDenied = true;
			boolean allAuthorized = true;

			Optional<ReturnOrder> returnOrderOptional = returnOrderRepository.findByRmaOrderNo(rmaNo);

			if (returnOrderOptional.isPresent()) {

				ReturnOrder returnOrderEntity = returnOrderOptional.get();
				Long returnOrderId = returnOrderEntity.getId();
				List<ReturnOrderItem> returnOrderItems = returnOrderItemRepository.findByReturnOrderId(returnOrderId);

				for (ReturnOrderItem returnOrderItem : returnOrderItems) {
					if (PortalConstants.RMCI.equalsIgnoreCase(returnOrderItem.getStatus())) {
						hasRequiresMoreCustomerInfo = true;
						// If any item requires more customer information, break the loop
						break;
					}
					if (PortalConstants.UNDER_REVIEW.equalsIgnoreCase(returnOrderItem.getStatus())) {
						hasUnderReview = true;
					}
					if (!PortalConstants.RMA_DENIED.equalsIgnoreCase(returnOrderItem.getStatus())) {
						// If any item is not Denied, set allDenied to false
						allDenied = false;
					}

					if (!(PortalConstants.AUTHORIZED_IN_TRANSIT.equalsIgnoreCase(returnOrderItem.getStatus())
							|| PortalConstants.AUTHORIZED_AWAITING_TRANSIT
									.equalsIgnoreCase(returnOrderItem.getStatus()))) {
						// If any item is not Authorized, set allAuthorized to false
						allAuthorized = false;
					}
				}

				if (hasRequiresMoreCustomerInfo) {
					returnOrderEntity.setStatus("Requires More Customer Information");
					// audit logs
					
					auditLog.setDescription(returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo()
							+ " has been updated to 'Requires More Customer Information'.Awaiting more information with customer.");
					auditLog.setHighlight("Requires More Customer Information");
					auditLog.setTitle("Return Order");
					auditLog.setStatus("RMA");
					auditLog.setRmaNo(rmaNo);
					auditLog.setUserName(updateBy);
					auditLogRepository.save(auditLog);
					// apply email functionality.
					String subject = PortalConstants.RMAStatus;
					String template = emailTemplateRenderer.getREQ_MORE_CUST_INFO();
					HashMap<String, String> map = new HashMap<>();
					map.put("order_contact_name", returnOrderEntity.getCustomer().getDisplayName());
					map.put("rma_status", returnOrderEntity.getStatus());
					map.put("rma", returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo());					try {
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}

				} else if (allDenied) {
					returnOrderEntity.setStatus("RMA Denied");
//					apply email functionality.
					String subject = PortalConstants.RMAStatus;
					String template = emailTemplateRenderer.getDENIED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("order_contact_name", returnOrderEntity.getCustomer().getDisplayName());
					map.put("rma_status", returnOrderEntity.getStatus());
					map.put("rma", returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo());
					try {

//						emailSender.sendEmail5(recipient, returnOrderEntity.getCustomer().getDisplayName(),
//								returnOrderEntity.getStatus());
						
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					// audit logs

					auditLog.setDescription(returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo() + " has been updated to 'DENIED'.");
					auditLog.setHighlight("DENIED");
					auditLog.setTitle("Return Order");
					auditLog.setStatus("RMA");
					auditLog.setRmaNo(rmaNo);
					auditLog.setUserName(updateBy);
					auditLogRepository.save(auditLog);

				} else if (allAuthorized) {
					returnOrderEntity.setStatus("Authorized");
					// audit logs
					auditLog.setDescription(returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo()
							+ " has been updated to 'AUTHORIZED'.The return is approved,Please proceed with the necessary steps.");
					auditLog.setHighlight("AUTHORIZED");
					auditLog.setTitle("Return Order");
					auditLog.setStatus("RMA");
					auditLog.setRmaNo(rmaNo);
					auditLog.setUserName(updateBy);
					auditLogRepository.save(auditLog);
					
					//Save in ERP
				    String apiUrl = "https://apiplay.labdepotinc.com/api/sales/orders/"+returnOrderEntity.getRmaOrderNo()+"/approve";
		            RestTemplate restTemplate = new RestTemplate();
		            HttpHeaders headers = new HttpHeaders();
	                try {
						headers.setBearerAuth(p21TokenServiceImpl.getToken());
					} catch (Exception e) {
							e.printStackTrace();
					}
	                HttpEntity<String> entity = new HttpEntity<>(headers);
	                ResponseEntity<String> response = restTemplate.exchange(
	                        apiUrl, HttpMethod.PUT, entity, String.class);
	                
	                if (response.getStatusCode() == HttpStatus.OK) {
	                	System.out.println("Saving Status Approved In ERP.");
	                }
	                else {
	                	System.out.println("There was an error while saving status in ERP.");
	                }
					
					
					//email
					String subject = PortalConstants.RMAStatus;
					String template = emailTemplateRenderer.getRMA_AUTHORIZED_TEMPLATE();
					HashMap<String, String> map = new HashMap<>();
					map.put("order_contact_name", returnOrderEntity.getCustomer().getDisplayName());
					map.put("rma_status", returnOrderEntity.getStatus());
					map.put("rma", returnOrderServiceImpl.getRmaaQualifier()+" "+returnOrderEntity.getRmaOrderNo());					try {
//						emailSender.sendEmail6(recipient, returnOrderEntity.getCustomer().getDisplayName(),
//								returnOrderEntity.getStatus());
						emailSender.sendEmail(recipient, template, subject, map);
					} catch (MessagingException e) {
						e.printStackTrace();
					}
				} else if (hasUnderReview) {
					returnOrderEntity.setStatus("Under Review");
				}

				returnOrderRepository.save(returnOrderEntity);

			}
			// update customer to put tracking code.
			String subject = PortalConstants.RMAStatus;
			HashMap<String, String> map1 = new HashMap<>();
			map1.put("LineItemStatus", updatedItem.getStatus());
			
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

			return "List Item Details Updated Successfully.";
		} else {
			throw new EntityNotFoundException("ReturnOrderItem with ID " + id + " not found");
		}

	}

	@Override
	public String updateNote(Long lineItemId, Long assignToId, String rmaNo, String updateBy,
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
			user.setRoles(user.getRoles());
			userRepository.save(user);

			existingItem.setFollowUpDate(updateNote.getFollowUpDate());
			existingItem.setNote(updateNote.getNote());
			existingItem.setUser(user);
			returnOrderItemRepository.save(existingItem);

			ReturnRoom returnRoom = new ReturnRoom();
			returnRoom.setName(updateBy);
			returnRoom.setMessage(updateNote.getNote());
			returnRoom.setAssignTo(user);
			returnRoom.setFollowUpDate(updateNote.getFollowUpDate());
			returnRoom.setStatus(updateNote.getStatus());
			returnRoom.setReturnOrderItem(existingItem);
			returnRoomRepository.save(returnRoom);

			AuditLog auditLog = new AuditLog();
			
			if (updateBy.equalsIgnoreCase(user.getFirstName() + " " + user.getLastName())) {
				auditLog.setDescription("A note has been assigned to " + user.getFirstName() + " " + user.getLastName()
						+ " of item - "+existingItem.getItemName()+". Please review the details and take necessary action.");	
			} else {
				auditLog.setDescription(updateBy + " has reassigned note to " + user.getFirstName() + " "
						+ user.getLastName() + " of item - "+existingItem.getItemName()+". Please review the details and take necessary action.");
			}
			auditLog.setTitle("Update Activity");
			auditLog.setHighlight("");
			auditLog.setStatus("Ordered Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);
			
			
			Date followUpDate = updateNote.getFollowUpDate();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd yyyy");
			String formattedDate = simpleDateFormat.format(followUpDate);
			String subject = PortalConstants.NOTE_STATUS;
			String template2 = emailTemplateRenderer.getVENDER_LINE_ITEM_STATUS();
			HashMap<String, String> map = new HashMap<>();
			
			map.put("AssignedUser", user.getFirstName());
			map.put("partNumber", existingItem.getItemName());
			map.put("vendorName", updateBy);
			map.put("LineItemStatus", updateNote.getStatus());
			map.put("date", formattedDate);
			try {
				emailSender.sendEmail(recipient, template2, subject, map);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
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
			ReturnOrderItem returnOrderItem =optionalReturnOrder.get();

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
//				stringBuilder.append("Email Address has been updated in shipping information of " + returnOrderItem.getItemName()
//						+ " by " + updateBy + ";");
//			}
//			String res = stringBuilder.toString();
			returnOrderItemRepository.save(returnOrderItem);
			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription("Shipping Information has been updated of item - "+returnOrderItem.getItemName()+" by "+updateBy+".");
			auditLog.setHighlight("");
			auditLog.setStatus("Ordered Items");
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
			if(preRestocking == null) {
				preRestocking = BigDecimal.valueOf(0);
			}
			preRestocking = preRestocking.setScale(0, BigDecimal.ROUND_DOWN);
			if(returnOrderItem.getAmount() == null) {
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
			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription(updateBy + " has been updated the restocking fee of item - " + returnOrderItem.getItemName()
            + " from $" + preRestocking + " to $" + returnOrderItem.getReStockingAmount() + ".");
			auditLog.setHighlight("");
			auditLog.setStatus("Ordered Items");
			auditLog.setRmaNo(rmaNo);
			auditLog.setUserName(updateBy);
			auditLogRepository.save(auditLog);
		}
		return "Restocking fee and return amount updated successfully";
	}

	@Override
	public List<StatusConfig> getAllStatus() {
		return statusConfigRepository.findAll();

	}

	@Override
	public List<QuestionConfig> getQuestions() {
		return questionConfigRepository.findAll();
	}

}