package com.continuum.constants;

public final class PortalConstants {

	private PortalConstants() {
	}

	public static final String SUCCESS = "Success";
	public static final String FAILED = "Failed";
	
	//status configurations
	public static final String UNDER_REVIEW = "Under Review";
	public static final String RETURN_REQUESTED = "Return Requested";
	public static final String AWAITING_VENDOR_APPROVAL = "Awaiting Vendor Approval";
	public static final String AWAITING_CARRIER_APPROVAL = "Awaiting Carrier Approval";
	public static final String AUTHORIZED_AWAITING_TRANSIT = "Authorized Awaiting Transit";
	public static final String AUTHORIZED_IN_TRANSIT = "Authorized In Transit";
	public static final String ITEM_RECIEVED_UNDER_REVIEW = "Item Recieved & Under Review";
	public static final String RET_CMPLT_CRED_APPLIED = "Return Completed-Credit Applied";
	public static final String RMA_DENIED = "RMA Denied";
	public static final String RMCI = "Requires More Customer Information";

	public static final String RMA = "RMA";
	public static final String EMAIL_RECIPIENT = "priyanshi.porwal@bytesfarms.com";
	public static final String EMAIL_SUBJECT_PREFIX = "Your RMA Return Order ";
	public static final String EMAIL_BODY_PREFIX = "Your RMA status is ";
	public static final String FPasswordLink = "Change Your Password.";
	public static final String ReturnOrderLineItemStatus = "Update Your Tracking Code.";
	public static final String RMAStatus = "Your RMA Status is Changed.";
	public static final String ASSIGN_RMA = "RMA has been assigned.";
	public static final String NOTE_STATUS = "A New Note Added";

	public static final String EMAIL_FROM = "ankit@techexprt.com";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
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
