package com.continuum.serviceImpl;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
	
	@Autowired
	ReturnOrderServiceImpl returnOrderServiceImpl;

	@Override
	public String forgetPassword(String email, HttpServletRequest request) {
		String uuid = UUID.randomUUID().toString();

		// Set the expiration time
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		Date expirationTime = calendar.getTime();

		User existingUser = userRepository.findByEmail(email);
		if (existingUser != null) {
			existingUser.setUuid(uuid);
			existingUser.setResetTokenExpiration(expirationTime);
			userRepository.save(existingUser);//saving user	
			try {
				this.sendEmail(email, uuid, request);
				return uuid;
			} catch (MessagingException me) {
				me.printStackTrace();
			}
		}
		
		return "Email not found";
	}

	public void sendEmail(String email, String uuid, HttpServletRequest request) throws MessagingException {
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
		String fullUrl = request.getRequestURL().toString();
		try {
			URL url = new URL(fullUrl);
			String host = url.getHost();
			String scheme = request.getScheme();
			String link = scheme + "://" + host + "/updatepassword?token=" + uuid;
//			String link = "http://labdepot.localhost:3000/updatepassword?token=" + uuid;
			String templateFilePath = PortalConstants.FPasswordLink;
			VelocityContext context = new VelocityContext();

			context.put("RESET_LINK", link);
			context.put("user_name", existingUser.getFirstName());
			context.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
			context.put("CLIENT_PHONE",String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));

			String renderedBody = EmailTemplateRenderer.renderFPasswordTemplate(context);

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(PortalConstants.EMAIL_RECIPIENT));
			message.setSubject(templateFilePath);
			message.setContent(renderedBody, "text/html");
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String updatePassword(String uuid, String password) {
		User user = userRepository.findByUuid(uuid);
		if (user != null) {
			// Check if the token has expired
			Date expirationTime = user.getResetTokenExpiration();
			Date currentTime = new Date();

			if (expirationTime != null && expirationTime.after(currentTime)) {
				// Token is not expired, allow password update
				String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
				user.setPassword(hashedPassword);
				user.setUuid(null);
				userRepository.save(user);
				return "Password Updated Successfully";
			} else {
				// Token has expired, show an error message
				return "Reset link has expired. Please request a new link.";
			}
		} else {
			return "User not found";
		}
	}

}