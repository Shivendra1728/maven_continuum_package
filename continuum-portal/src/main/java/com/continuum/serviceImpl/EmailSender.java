package com.continuum.serviceImpl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sound.sampled.Port;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.continuum.constants.PortalConstants;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.helper.P21OrderData;


@Component
public class EmailSender {
	@Autowired
	UserRepository userRepository;

	
	@Value(PortalConstants.MAIL_HOST)
	private String mailHost;

	@Value(PortalConstants.MAIL_PORT)
	private int mailPort;

	@Value(PortalConstants.MAIL_USERNAME)
	private String mailUsername;

	@Value(PortalConstants.MAIL_PASSWORD)
	private String mailPassword;

	public void sendEmail(String recipient, String subject, String body, ReturnOrderDTO returnOrderDTO, CustomerDTO customerDTO)
			throws MessagingException {
		Session session = createSession();

		VelocityContext context = new VelocityContext();
		
		if(returnOrderDTO.getStatus().equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
			context.put("status", returnOrderDTO.getStatus());
			context.put("rma_order_no", returnOrderDTO.getRmaOrderNo());	
		}
		else {
			context.put("status", returnOrderDTO.getStatus());
			context.put("rma_order_no", "null");
		}
		
		context.put("order_contact_name",customerDTO.getDisplayName());
		context.put("order_no", returnOrderDTO.getOrderNo());

		String templateFilePath = PortalConstants.EMAIL_TEMPLATE_FILE_PATH;
		String renderedBody = EmailTemplateRenderer.renderTemplate(context);


		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");


		Transport.send(message);
	}

	
	public void sendEmail2(String recipient,String updatedRMAStatus) throws MessagingException {
		
		Session session = createSession();
		
		String templateFilePath = PortalConstants.RMAStatus;
		VelocityContext context = new VelocityContext();
		context.put("rma_status",updatedRMAStatus);
		context.put("message", "This is a sample Message");
		
		
		String renderedBody = EmailTemplateRenderer.renderRMAStatusChangeTemplate(context);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}
	
	public void sendEmailToVender(String recipient, String LineItemStatus) throws MessagingException {
		Session session = createSession();
		
		String templateFilePath = PortalConstants.RMAStatus;
		VelocityContext context = new VelocityContext();
		context.put("AssignedUser", "Sample user");
		context.put("partNumber", "Sample Name");
		context.put("vendorName", "Sample Vendername");
		context.put("LineItemStatus", LineItemStatus);
		
		String renderedBody = EmailTemplateRenderer.renderVenderLineItemtatus(context);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}
	
	public void emailToCustomer(String email, String name, String formattedDate) throws MessagingException {
		User existingUser = userRepository.findByEmail(email);

		Session session = createSession();

		String templateFilePath = PortalConstants.NOTE_STATUS;
		VelocityContext context = new VelocityContext();

		context.put("name", name);
		context.put("date", formattedDate);
		String renderedBody = EmailTemplateRenderer.renderCustomerNoteStatus(context);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}
	
	public void sendEmailForUpdateItemStatus(String email, String LineItemStatus) throws MessagingException {
		User existingUser = userRepository.findByEmail(email);

		Session session = createSession();

		String templateFilePath = PortalConstants.ReturnOrderLineItemStatus;
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
	
	public Session createSession() {
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
		return session;
	}
}