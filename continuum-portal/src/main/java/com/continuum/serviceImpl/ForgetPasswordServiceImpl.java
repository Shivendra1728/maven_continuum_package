package com.continuum.serviceImpl;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.continuum.constants.PortalConstants;
import com.continuum.service.ForgetPasswordService;
import com.continuum.tenant.repos.entity.Role;
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
	
	EmailTemplateRenderer emailTemplateRenderer = new EmailTemplateRenderer();
	
	@Autowired
	EmailSender emailSender;

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
			
			String recipient = email;
			String subject = PortalConstants.FPasswordLink;
			String template = emailTemplateRenderer.getFPASSWORD_TEMPLETE_CONTENT();
			HashMap<String, String> map = new HashMap<>();
			String fullUrl = request.getRequestURL().toString();
			try {
			URL url = new URL(fullUrl);
			String host = url.getHost();
			String scheme = request.getScheme();
			String link = scheme + "://" + host + "/updatepassword?token=" + uuid;
//			String link="http://pace.localhost:3000/updatepassword?token="+uuid;
			map.put("RESET_LINK", link);
			map.put("user_name", returnOrderServiceImpl.getRmaaQualifier());
			map.put("CLIENT_MAIL", returnOrderServiceImpl.getClientConfig().getEmailFrom());
			map.put("CLIENT_PHONE",
					String.valueOf(returnOrderServiceImpl.getClientConfig().getClient().getContactNo()));
			try {
				emailSender.sendEmail(recipient, template, subject, map);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			}catch(Exception e) {
				e.printStackTrace();
			}
			userRepository.save(existingUser);//saving user	
			return uuid;
		}
		
		return "Email not found";
	}


		public Role updatePassword(String uuid, String password) {
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
					return user.getRole();
				} else {
					// Token has expired, show an error message
					return null;
				}
			} else {
				return null;
			}
		}

}