package com.continuum.serviceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityNotFoundException;

import org.apache.velocity.VelocityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.continuum.constants.PortalConstants;
import com.continuum.service.ReturnOrderItemService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.ReturnRoom;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnRoomRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.ReturnOrderItemDTO;

@Service
public class ReturnOrderItemServiceImpl implements ReturnOrderItemService {
	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;
	@Autowired
	UserRepository userRepository;

	@Autowired
	AuditLogRepository auditLogRepository;

	@Autowired
	ReturnRoomRepository returnRoomRepository;

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

	@Override
	public String updateReturnOrderItem(Long id, ReturnOrderItemDTO updatedItem) {
		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(id);

		if (optionalItem.isPresent()) {
			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
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
				auditLog.setDescription(existingItem.getUser().getFullName() + " has changed status of "
						+ existingItem.getItemName() + " from " + previousStatus + " to " + updatedItem.getStatus());
				auditLog.setHighlight("status");
				auditLog.setStatus("Ordered Items");

			}
			if (updatedItem.getProblemDesc() != null) {
				existingItem.setProblemDesc(updatedItem.getProblemDesc());
				auditLog.setDescription(existingItem.getUser().getFullName()
						+ " has updated the problem description of " + existingItem.getItemName());
				auditLog.setHighlight("problem description");
				auditLog.setStatus("Ordered Items");
			}
			if (updatedItem.getReasonCode() != null) {
				existingItem.setReasonCode(updatedItem.getReasonCode());
				auditLog.setDescription(existingItem.getUser().getFullName() + " has updated the reason code of "
						+ existingItem.getItemName());
				auditLog.setHighlight("reason code");
				auditLog.setStatus("Ordered Items");
			}

			if (updatedItem.getTrackingUrl() != null) {
				existingItem.setTrackingUrl(updatedItem.getTrackingUrl());
				auditLog.setDescription(existingItem.getUser().getFullName() + " has Updated URL of "
						+ existingItem.getItemName() + " to " + updatedItem.getTrackingUrl());
				auditLog.setHighlight("URL");
				auditLog.setStatus("Ordered Items");
			}
			if (updatedItem.getTrackingNumber() != null) {
				existingItem.setTrackingNumber(updatedItem.getTrackingNumber());
				auditLog.setDescription(existingItem.getUser().getFullName() + " has Updated Tracking Number of "
						+ existingItem.getItemName() + " to " + updatedItem.getTrackingNumber());
				auditLog.setHighlight("Tracking Number");
				auditLog.setStatus("Ordered Items");
			}
			if (updatedItem.getCourierName() != null) {
				existingItem.setCourierName(updatedItem.getCourierName());
				auditLog.setDescription(existingItem.getUser().getFullName() + " has Updated Courier Name of "
						+ existingItem.getItemName() + " to " + updatedItem.getCourierName());
				auditLog.setHighlight("URL");
				auditLog.setStatus("Ordered Items");
			}

			returnOrderItemRepository.save(existingItem);

			auditLogRepository.save(auditLog);

			if ("Approved_Awaiting_Transit".equals(updatedItem.getStatus())) {
				try {
					sendEmail1(recipient, updatedItem.getStatus());
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
	public String updateNote(Long lineItemId, Long assignToId, ReturnOrderItemDTO updateNote) {
		Optional<ReturnOrderItem> optionalItem = returnOrderItemRepository.findById(lineItemId);
		Optional<User> auditUserDetails = userRepository.findById(optionalItem.get().getUser().getId());
		User auditUser = auditUserDetails.get();
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
			returnRoom.setName(auditUser.getFirstName() + " " + auditUser.getLastName());
			returnRoom.setMessage(updateNote.getNote());
			returnRoom.setAssignTo(user);
			returnRoom.setFollowUpDate(updateNote.getFollowUpDate());
			returnRoom.setStatus(updateNote.getStatus());
			returnRoomRepository.save(returnRoom);

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Returned Activity");
			auditLog.setDescription(auditUser.getFirstName() + " " + auditUser.getLastName()
					+ " as added a new note in the ordered item - " + existingItem.getItemName());
			auditLog.setHighlight("note");
			auditLog.setStatus("Ordered Items");
			auditLogRepository.save(auditLog);
			try {
				sendNoteEmail1(recipient, auditUser.getFirstName() + " " + auditUser.getLastName());

			} catch (MessagingException e) {
				e.printStackTrace();
			}

			return "Updated Note Details and capture in return room and audit log";

		} else {
			return "Not Found";

		}

	}

	public void sendEmail1(String email, String LineItemStatus) throws MessagingException {
		User existingUser = userRepository.findByEmail(email);

		Properties props = new Properties();

		props.put(PortalConstants.SMTP_HOST, mailHost);
		props.put(PortalConstants.SMTP_PORT, mailPort);
		props.put(PortalConstants.SMTP_AUTH, PortalConstants.TRUE);
		props.put(PortalConstants.SMTP_STARTTLS_ENABLE, PortalConstants.TRUE); // Enable STARTTLS

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		String templateFilePath = PortalConstants.FPasswordLink;
		VelocityContext context = new VelocityContext();

		context.put("LineItemStatus", LineItemStatus);

		String renderedBody = EmailTemplateRenderer.renderStatusChangeTemplate(context);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}

	public void sendNoteEmail1(String email, String name) throws MessagingException {
		User existingUser = userRepository.findByEmail(email);

		Properties props = new Properties();

		props.put(PortalConstants.SMTP_HOST, mailHost);
		props.put(PortalConstants.SMTP_PORT, mailPort);
		props.put(PortalConstants.SMTP_AUTH, PortalConstants.TRUE);
		props.put(PortalConstants.SMTP_STARTTLS_ENABLE, PortalConstants.TRUE); // Enable STARTTLS

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		String templateFilePath = PortalConstants.NOTE_STATUS;
		VelocityContext context = new VelocityContext();

		context.put("name", name);

		String renderedBody = EmailTemplateRenderer.renderNoteStatusChangeTemplate(context);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}

	@Override
	public String updateShipTo(Long rtnOrdId, OrderAddress orderAddress) {

		Optional<ReturnOrderItem> ro = returnOrderItemRepository.findById(rtnOrdId);

		if (ro.isPresent()) {
			ReturnOrderItem returnOrderItem = ro.get();

			returnOrderItem.getShipTo().setAddressType(orderAddress.getAddressType());
			returnOrderItem.getShipTo().setFax(orderAddress.getFax());
			returnOrderItem.getShipTo().setStreet1(orderAddress.getStreet1());
			returnOrderItem.getShipTo().setStreet2(orderAddress.getStreet2());
			returnOrderItem.getShipTo().setZipcode(orderAddress.getZipcode());
			returnOrderItem.getShipTo().setCity(orderAddress.getCity());
			returnOrderItem.getShipTo().setCountry(orderAddress.getCountry());
			returnOrderItem.getShipTo().setProvince(orderAddress.getProvince());
			returnOrderItem.getShipTo().setPhoneNumber(orderAddress.getPhoneNumber());

			returnOrderItemRepository.save(returnOrderItem);

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription(returnOrderItem.getUser().getFullName() + "has updated shipping info for id: "
					+ returnOrderItem.getShipTo().getId());
			auditLog.setHighlight("shipping info");
			auditLog.setStatus("Ordered Items");
			auditLogRepository.save(auditLog);

			return "Shipping info update";
		} else {
			return "not found";
		}
	}

	@Override
	public String updateRestockingFee(Long id, BigDecimal reStockingAmount) {
		Optional<ReturnOrderItem> returnorderitem = returnOrderItemRepository.findById(id);
		if (returnorderitem.isPresent()) {
			ReturnOrderItem roi = returnorderitem.get();

			BigDecimal newReturnAmoun = roi.getAmount().subtract(reStockingAmount);

			roi.setReStockingAmount(reStockingAmount);
			roi.setReturnAmount(newReturnAmoun);

			returnOrderItemRepository.save(roi);
			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Update Activity");
			auditLog.setDescription(roi.getUser().getFullName() + "has updated restocking fee for item: "
					+ roi.getItemName()+ " with id :"+roi.getShipTo().getId());
			auditLog.setHighlight("restocking fee");
			auditLog.setStatus("Ordered Items");
			auditLogRepository.save(auditLog);
		}
		return "Restocking fee and return amount updated successfully";
	}
}