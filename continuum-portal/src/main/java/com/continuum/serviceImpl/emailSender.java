package com.continuum.serviceImpl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.di.commons.dto.ReturnOrderDTO;

@Component
public class emailSender {
	@Value("${spring.mail.host}")
	private String mailHost;

	@Value("${spring.mail.port}")
	private int mailPort;

	@Value("${spring.mail.username}")
	private String mailUsername;

	@Value("${spring.mail.password}")
	private String mailPassword;

	private static final String EMAIL_FROM = "shivendra.bais@techexprt.com";

	private ReturnOrderDTO returnOrderDTO;

	@Autowired
	public emailSender(ReturnOrderDTO returnOrderDTO) {
		this.returnOrderDTO = returnOrderDTO;
	}

	public void sendEmail(String recipient, String subject, String body, ReturnOrderDTO returnOrderDTO)
			throws MessagingException {
		Properties props = new Properties();

		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		VelocityContext context = new VelocityContext();
		context.put("status", returnOrderDTO.getStatus());
		context.put("rma_order_no", returnOrderDTO.getRmaOrderNo());
		context.put("order_no", returnOrderDTO.getOrderNo());

		String templateFilePath = "src/main/resources/email_template.vm";
		String renderedBody = EmailTemplateRenderer.renderTemplate(templateFilePath, context);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");

		System.out.println(renderedBody);

		Transport.send(message);
	}
}