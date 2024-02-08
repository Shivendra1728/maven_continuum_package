package com.continuum.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import com.di.integration.p21.serviceImpl.RmaReceiptServiceImpl;

import org.apache.commons.io.FileUtils;

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
		List<File> downloadedFiles = new ArrayList<File>();
		// Add multiple attachments if provided
		if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
			for (String fileName : attachmentPaths.keySet()) {
				File downloadedFile = downloadFile(attachmentPaths.get(fileName), fileName);
				downloadedFiles.add(downloadedFile);
				String filePath = downloadedFile.getAbsolutePath();
		        BodyPart attachmentBodyPart = new MimeBodyPart();
		        filePath = filePath.replace("\\", "/");
		        DataSource source = new FileDataSource(filePath); // Replace with your file path
		        
		        
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
		if(downloadedFiles != null) {
			for(File file : downloadedFiles) {
				deleteFile(file);
			}
		}
	}
	
	private void deleteFile(File file) throws IOException {
		if (file.exists() && !file.isDirectory()) {	
			logger.info("Deleting attachment: "+file.getAbsolutePath());
            Files.deleteIfExists(file.toPath());
        }	
	}

	public static File downloadFile(String url, String localFileName) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        File file = new File(localFileName);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to download file: " + response.getStatusLine());
            }
            logger.info("Downloading Email Attachment" + url);
            // Save the file content to a local file
            FileUtils.copyInputStreamToFile(response.getEntity().getContent(), file);
        }

        return file;
    }

}