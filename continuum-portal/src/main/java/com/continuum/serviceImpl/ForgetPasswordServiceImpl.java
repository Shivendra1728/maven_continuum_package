package com.continuum.serviceImpl;

import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.continuum.constants.PortalConstants;
import com.continuum.service.ForgetPasswordService;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.UserRepository;

@Service
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

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

	@Value(PortalConstants.EMAIL_RECIPIENT)
	private String recipient;

	@Override
	public String forgetPassword(String email) {
		String uuid = UUID.randomUUID().toString();

		User existingUser = userRepository.findByEmail(email);
		if (existingUser != null) {
			existingUser.setUuid(uuid);
			userRepository.save(existingUser);
		}
		try {
			this.sendEmail(email, uuid);

		} catch (MessagingException me) {
			me.printStackTrace();
		}

		return uuid;
	}

	public void sendEmail(String email, String uuid) throws MessagingException {
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
		String resetUrl = "http://localhost:8080/continuum/update/passwords?token=" + uuid;
		String templateFilePath = PortalConstants.FPasswordLink;
		VelocityContext context = new VelocityContext();
		context.put("user_name", existingUser.getFirstName().toUpperCase());
		context.put("uuid", uuid);
		context.put("resetUrl", resetUrl);

		String renderedBody = EmailTemplateRenderer.renderFPasswordTemplate(context);

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject(templateFilePath);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);

	}

	public String updatePassword(String uuid, String password) {
		User user = userRepository.findByUuid(uuid);
		if (user != null) {
			user.setPassword(password);
			userRepository.save(user);
			return "Password Updated Successfully";
		} else {
			return "User not found";
		}
	}

}