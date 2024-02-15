package com.di.integration.p21.serviceImpl;

import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.hibernate.dialect.function.TemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.di.integration.constants.IntegrationConstants;

@Component
public class SendMail {
	@Value(IntegrationConstants.MAIL_HOST)
	private String mailHost;

	@Value(IntegrationConstants.MAIL_PORT)
	private int mailPort;

	@Value(IntegrationConstants.MAIL_USERNAME)
	private String mailUsername;

	@Value(IntegrationConstants.MAIL_PASSWORD)
	private String mailPassword;

	public void sendEmail(String recipient, String template, String subject, HashMap<String, String> map)
			throws MessagingException {
		Properties props = new Properties();

		props.put(IntegrationConstants.SMTP_HOST, mailHost);
		props.put(IntegrationConstants.SMTP_PORT, mailPort);
		props.put(IntegrationConstants.SMTP_AUTH, IntegrationConstants.TRUE);
		props.put(IntegrationConstants.SMTP_STARTTLS_ENABLE, IntegrationConstants.TRUE);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		VelocityContext context = new VelocityContext();
		for (String key : map.keySet()) {
			context.put(key, map.get(key));
		}

		String renderedBody = TemplateRenderrer.renderer(template, context);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(IntegrationConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);
	}

}