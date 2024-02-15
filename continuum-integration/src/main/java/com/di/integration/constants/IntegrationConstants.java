package com.di.integration.constants;

public final class IntegrationConstants {
    
	private IntegrationConstants() {
    }
    
    
    public static final String SUCCESS = "Success";
    public static final String FAILED = "Failed";
    public static final String UNDER_REVIEW = "Under Review";
    public static final String RMA = "RMA";
  
 // DataElement constants
    public static final String DATA_ELEMENT_NAME_ORDER = "TABPAGE_1.order";
    public static final String DATA_ELEMENT_TYPE_FORM = "Form";
    public static final String DATA_ELEMENT_NAME_ORDER_ITEMS = "TP_ITEMS.items";
    public static final String DATA_ELEMENT_NAME_TP_EXTDINFO = "TP_EXTDINFO.extd_info";
    public static final String DATA_ELEMENT_TYPE_LIST = "List";
    public static final String DATA_ELEMENT_NAME_REASON_CODES = "REASONCODESHDR.reasoncodeshdr";
    
    // Edit constants
    public static final String COMPANY_ID = "company_id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String SALES_LOC_ID = "sales_loc_id";
    public static final String SHIP_TO_ID = "ship_to_id";
    public static final String CONTACT_ID = "contact_id";
    public static final String PO_NO = "po_no";
    public static final String TAKER = "taker";
    public static final String OE_ORDER_ITEM_ID = "oe_order_item_id";
    public static final String UNIT_QUANTITY = "unit_quantity";
    public static final String MAX_LINE_NO = "max_line_no";

    
    public static final String HDR_NOTE = "HDR_NOTE.hdr_note";
    public static final String LIST = "List";
    public static final String NOTE_ID = "note_id";
    public static final String TOPIC = "topic";
    public static final String NOTE = "note";
    public static final String NOTEPAD_CLASS_DESC = "notepad_class_desc";
    public static final String OTHER="OTHER";
    
    // Key Names
    public static final String LOCATION_ID = "location_id";
    public static final String LOST_SALES_ID = "lost_sales_id";
    public static final String ORDER_NO = "order_no";
    public static final String CC_INVOICE_NO_DISPLAY = "cc_invoice_no_display";
    

    public static final String ERP_DATA_API_BASE_URL = "${erp.data_api_base_url}";
    public static final String ERP_PRODUCT_API_BASE_URL = "${erp.product_api}";
    public static final String ERP_DATA_API_ORDER_LINE = "${erp.data_api_order_line}";
    public static final String ERP_ORDER_LINE_SELECT_FIELDS = "${erp.order_line_select_fields}";
    public static final String ERP_ORDER_FORMAT = "${erp.order_format}";
    public static final String ERP_RMA_CREATE_API = "${erp.rma.create}";
    public static final String ERP_RMA_UPDATE_RESTOCKING_API = "${erp.data_update_restocking_url}";
    public static final String ERP_GET_RETURN_LOCATIONS = "${erp.data_get_location_url}";
    public static final String ERP_SERIALIZED_API = "${erp.serialized_url}";
    public static final String ERP_SELLABLE_API = "${erp.sellable_url}";
    public static final String ERP_DATA_API_SERIAL_LINE = "${erp.serial_numbers}";
    
    //Making customized constants for scheduler
    public static final String ERP_TRANSACTION_GET = "/uiserver0/api/v2/transaction/get";
    public static final String ERP_RMA_ENDPOINT = "/uiserver0/api/v2/transaction"; 
    
    
    public static final String ERP_DATA_API_ORDER_VIEW = "${erp.data_api_order_view}";
    public static final String ERP_DATA_API_INVOICE_VIEW="${erp.data_api_invoice_view}";
    public static final String ERP_DATA_API_INVOICE_LINE_VIEW = "${erp.data_api_invoice_line_view}";
    public static final String ERP_ORDER_SELECT_FIELDS = "${erp.order_select_fields}";
    public static final String ERP_DATA_P21_OE_ORDER_VIEW = "${erp.p21_data_order_view}";

    
    // Filter fields
    public static final String ORIGINAL_INVOICE_NO = "original_invoice_no";
    public static final String INVOICE_NO = "invoice_no";
    public static final String EMAIL_ADDRESS = "email_address";

    // Filter condition operators
    public static final String CONDITION_EQ = "eq";

    // Filter logical operators
    public static final String AND = " and ";
    
    public static final String ENDPOINT_VIEW_CONTACTS = "p21_view_contacts";

    public static final String FILTER_CONDITION_SHIP2_ZIP = "ship2_zip";
    public static final String FILTER_CONDITION_PO_NUMBER = "po_number";
    public static final String FILTER_CONDITION_MAIL_POSTAL_CODE_A = "mail_postal_code_a";
    public static final String ORDER_DATE = "order_date";
    public static final String FILTER_CONDITION_GE = "ge";
    public static final String DATETIME_PREFIX = "datetime'";
    public static final String DATETIME_SUFFIX = "'";
    
    public static final String CONTINUUM = "Continuum";

    // P21 Token service Impl
    public static final String ERP_USERNAME = "${erp.username}";
    public static final String ERP_PASSWORD = "${erp.password}";
    public static final String ERP_TOKEN_ENDPOINT = "${erp.token_end_point}";
    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String ACCESS_TOKEN_ELEMENT = "AccessToken";
    public static final String BILL_TO_POSTAL_CODE = "bill2_postal_code";
	public static final String APPROVED ="approved";
	public static final String ERP_RMA_WINDOW_ENDPOINT ="${erp.window_end_point}";
	
	
	
	public static final String MAIL_HOST = "${spring.mail.host}";
	public static final String MAIL_PORT = "${spring.mail.port}";
	public static final String MAIL_USERNAME = "${spring.mail.username}";
	public static final String MAIL_PASSWORD = "${spring.mail.password}";
	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String SMTP_PORT = "mail.smtp.port";
	public static final String SMTP_AUTH = "mail.smtp.auth";
	public static final String TRUE = "true";
	public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
	public static final String EMAIL_FROM = "shivendra.bais@bytesfarms.com";	
	
    


}

