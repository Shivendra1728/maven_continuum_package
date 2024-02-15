package com.di.integration.p21.serviceImpl;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderrer {

	private static final String INVOICE_LINK_FAILED_TEMPLATE = "<!DOCTYPE html>\r\n" + "<html lang=\"en\">\r\n" + "\r\n"
			+ "<head>\r\n" + "    <title></title>\r\n"
			+ "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n"
			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + "    <style>\r\n"
			+ "        * {\r\n" + "            box-sizing: border-box;\r\n" + "            margin: 0;\r\n"
			+ "            padding: 0;\r\n" + "        }\r\n" + "\r\n" + "        body {\r\n"
			+ "            background-color: #f5f5f5;\r\n" + "            -webkit-text-size-adjust: none;\r\n"
			+ "            text-size-adjust: none;\r\n" + "        }\r\n" + "\r\n" + "        p {\r\n"
			+ "            line-height: 1.5; /* Increased line height for better readability */\r\n"
			+ "            margin-bottom: 15px; /* Increased margin between paragraphs */\r\n"
			+ "            padding: 10px; /* Added padding to paragraphs */\r\n" + "        }\r\n" + "\r\n"
			+ "        .container {\r\n" + "            width: 100%;\r\n" + "            background-color: #f5f5f5;\r\n"
			+ "            padding: 20px; /* Added padding to container */\r\n" + "        }\r\n" + "\r\n"
			+ "        .row {\r\n" + "            width: 100%;\r\n" + "            max-width: 650px;\r\n"
			+ "            margin: 0 auto;\r\n" + "            background-color: #fff;\r\n"
			+ "            color: #000;\r\n" + "            padding: 20px; /* Added padding to row */\r\n"
			+ "            border-radius: 10px; /* Added border radius for rounded corners */\r\n" + "        }\r\n"
			+ "\r\n" + "        .column {\r\n" + "            width: 100%;\r\n" + "            font-weight: 400;\r\n"
			+ "            text-align: left;\r\n" + "            border: none;\r\n" + "        }\r\n" + "\r\n"
			+ "        .paragraph {\r\n" + "            color: #052D3D;\r\n"
			+ "            font-family: 'Lato', Tahoma, Verdana, Segoe, sans-serif;\r\n"
			+ "            font-size: 12px;\r\n" + "            line-height: 120%;\r\n"
			+ "            word-break: break-word;\r\n" + "        }\r\n" + "\r\n" + "        .paragraph strong {\r\n"
			+ "            font-weight: bold;\r\n" + "        }\r\n" + "    </style>\r\n" + "</head>\r\n" + "\r\n"
			+ "<body>\r\n" + "    <div class=\"container\">\r\n" + "        <div class=\"row\">\r\n"
			+ "            <div class=\"column\">\r\n"
			+ "                <div class=\"paragraph\" style=\"margin-bottom:10px;\">\r\n"
			+ "                    <strong>Invoice linking for the above RMA and line item has failed through the scheduler</strong><br>\r\n"
			+ "                </div>\r\n" + "                <div class=\"paragraph\" >\r\n"
			+ "                    <strong>RMA Number :</strong> ${rmaNumber}<br>\r\n" + "                </div>\r\n"
			+ "                <div class=\"paragraph\">\r\n"
			+ "                    <strong>Line Item :</strong> ${lineItem}<br>\r\n" + "                </div>\r\n"
			+ "                <div class=\"paragraph\">\r\n"
			+ "                    <strong>Invoice Number to link was :</strong> ${invoiceNumber}<br>\r\n"
			+ "                </div>\r\n" + "                <div class=\"paragraph\">\r\n"
			+ "                    <strong>The tenant for which RMA was failed :</strong> ${Tenant}<br>\r\n"
			+ "                </div>\r\n" + "                <div class=\"paragraph\"> <strong>Error :</strong> \r\n"
			+ "                    ${invoiceLinkingError}<br>\r\n"
			+ "                    Please try manually link it if possible.\r\n" + "                </div>\r\n"
			+ "            </div>\r\n" + "        </div>\r\n" + "    </div>\r\n" + "</body>\r\n" + "\r\n"
			+ "</html>\r\n" + "";

	public static String getInvoice_Link_Failed_Template() {
		return INVOICE_LINK_FAILED_TEMPLATE;
	}

	public static String renderer(String template, VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", template);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}