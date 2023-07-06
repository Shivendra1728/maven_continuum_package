package com.continuum.serviceImpl;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
public class emailSender {

	private static final String SMTP_HOST = "smtp.gmail.com";
	private static final int SMTP_PORT = 587;
	private static final String SMTP_USERNAME = "shivendrasinghbais14@gmail.com";
	private static final String SMTP_PASSWORD = "grdtfdpcpealhmhe";
	private static final String EMAIL_FROM = "shivendra.bais@techexprt.com";
	public static void sendEmail(String recipient, String subject, String body) throws MessagingException {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", SMTP_HOST);
		props.put("mail.smtp.port", SMTP_PORT);
		Session session = Session.getInstance(props, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);

			}

		});

		Message message = new MimeMessage(session);

		message.setFrom(new InternetAddress(EMAIL_FROM));

		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

		message.setSubject(subject);

		message.setText(body);

		Transport.send(message);

	}

}
