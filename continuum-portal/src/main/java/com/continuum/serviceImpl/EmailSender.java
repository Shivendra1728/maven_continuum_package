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
import com.di.commons.dto.ReturnOrderDTO;


@Component
public class EmailSender {
	
	@Value(PortalConstants.MAIL_HOST)
	private String mailHost;

	@Value(PortalConstants.MAIL_PORT)
	private int mailPort;

	@Value(PortalConstants.MAIL_USERNAME)
	private String mailUsername;

	@Value(PortalConstants.MAIL_PASSWORD)
	private String mailPassword;

    @Autowired
	ReturnOrderDTO returnOrderDTO;

	@Autowired
	public EmailSender(ReturnOrderDTO returnOrderDTO) {
		this.returnOrderDTO = returnOrderDTO;
	}

	public void sendEmail(String recipient, String subject, String body, ReturnOrderDTO returnOrderDTO)
			throws MessagingException {
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

		VelocityContext context = new VelocityContext();
		
		if(returnOrderDTO.getStatus().equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
			context.put("status", returnOrderDTO.getStatus());
			context.put("rma_order_no", returnOrderDTO.getRmaOrderNo());	
		}
		else {
			context.put("status", returnOrderDTO.getStatus());
			context.put("rma_order_no", "null");
		}
		
		context.put("order_no", returnOrderDTO.getOrderNo());

		String templateFilePath = PortalConstants.EMAIL_TEMPLATE_FILE_PATH;
		String renderedBody = EmailTemplateRenderer.renderTemplate(context);


		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");

	//	System.out.println(renderedBody);

		Transport.send(message);
	}
}