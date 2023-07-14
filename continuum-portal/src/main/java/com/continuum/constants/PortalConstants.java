package com.continuum.constants;

public final class PortalConstants {

	private PortalConstants() {
	}

	public static final String SUCCESS = "Success";
	public static final String FAILED = "Failed";
	public static final String UNDER_REVIEW = "Under Review";
	public static final String RMA = "RMA";
	public static final String EMAIL_RECIPIENT = "dhawal.bahe@techexprt.com";
	public static final String EMAIL_SUBJECT_PREFIX = "Your RMA Return Order ";
	public static final String EMAIL_BODY_PREFIX = "Your RMA status is ";
	public static final String EMAIL_FROM = "shivendra.bais@techexprt.com";
	public static final String TRUE ="true";
	public static final String FALSE ="false";
	// SMTP properties
	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

	// Email template file path
	public static final String EMAIL_TEMPLATE_FILE_PATH = "resources/email_template.vm";
	
	
	public static final String MAIL_HOST = "${spring.mail.host}";
    public static final String MAIL_PORT = "${spring.mail.port}";
    public static final String MAIL_USERNAME = "${spring.mail.username}";
    public static final String MAIL_PASSWORD = "${spring.mail.password}";
    

}
