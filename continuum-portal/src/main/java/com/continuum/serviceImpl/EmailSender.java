package com.continuum.serviceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.continuum.constants.PortalConstants;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

@Component
public class EmailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
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

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	public void sendEmail(String recipient, String template, String subject, HashMap<String, String> map)
			throws AddressException, MessagingException {
		Properties props = new Properties();

		props.put(PortalConstants.SMTP_HOST, mailHost);
		props.put(PortalConstants.SMTP_PORT, mailPort);
		props.put(PortalConstants.SMTP_AUTH, PortalConstants.TRUE);
		props.put(PortalConstants.SMTP_STARTTLS_ENABLE, PortalConstants.TRUE); // Enable STARTTLS

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId= httpServletRequest.getHeader("tenant");
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		mailUsername = masterTenant.getEmailUsername();
		mailPassword = masterTenant.getEmailPassword();

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		VelocityContext context = new VelocityContext();
		for (String key : map.keySet()) {
			context.put(key, map.get(key));
		}

		String renderedBody = EmailTemplateRenderer.renderer(template, context);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);
		message.setContent(renderedBody, "text/html");
		Transport.send(message);
	}
	
	public void sendEmailWithAttachment(String recipient, String template, String subject, HashMap<String, String> map,
			Map<String, String> attachmentPaths) throws AddressException, MessagingException, IOException {
		Properties props = new Properties();
		props.put(PortalConstants.SMTP_HOST, mailHost);
		props.put(PortalConstants.SMTP_PORT, mailPort);
		props.put(PortalConstants.SMTP_AUTH, PortalConstants.TRUE);
		props.put(PortalConstants.SMTP_STARTTLS_ENABLE, PortalConstants.TRUE);

		// Obtain tenantId from the HTTP request headers
		String tenantId = httpServletRequest.getHeader("tenant");
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenantId);
		mailUsername = masterTenant.getEmailUsername();
		mailPassword = masterTenant.getEmailPassword();

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(mailUsername, mailPassword);
			}
		});

		VelocityContext context = new VelocityContext();
		for (String key : map.keySet()) {
			context.put(key, map.get(key));
		}

		String renderedBody = EmailTemplateRenderer.renderer(template, context);

		// Create a message with attachments
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(PortalConstants.EMAIL_FROM));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
		message.setSubject(subject);

		// Create the message body part
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(renderedBody, "text/html");

		// Create a multipart message to combine text and attachments
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		// Add multiple attachments if provided
		if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
			for (String fileName : attachmentPaths.keySet()) {
				byte[] downloadFile = downloadFile(attachmentPaths.get(fileName));
		        BodyPart attachmentBodyPart = new MimeBodyPart();
		        DataSource source = new ByteArrayDataSource(downloadFile, "application/octet-stream"); // Replace with your file path   
		        // Create a DataHandler for the attachment
		        attachmentBodyPart.setDataHandler(new DataHandler(source));

		        // Set the file name for the attachment
		        attachmentBodyPart.setFileName(fileName);

		        // Add the attachment body part to the multipart
		        multipart.addBodyPart(attachmentBodyPart);
		    }
		}

		// Set the content of the message to the multipart
		message.setContent(multipart);

		// Send the message
		Transport.send(message);
	}
	
	private byte[] downloadFile(String fileUrl) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(fileUrl);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    if (httpEntity != null) {
                        return EntityUtils.toByteArray(httpEntity);
                    } else {
                        throw new IOException("Empty response entity");
                    }
                } else {
                    throw new RuntimeException("Failed to download file. Status code: " + statusCode);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file. Exception: " + e.getMessage(), e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
            }
        }
    }

}