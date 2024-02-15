package com.di.integration.p21.serviceImpl;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderrer {

	private static final String INVOICE_LINK_FAILED_TEMPLATE = "<!DOCTYPE html>\r\n"
	        + "<html xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" lang=\"en\">\r\n"
	        + "\r\n" + "<head>\r\n" + "    <title></title>\r\n"
	        + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n"
	        + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><!--[if mso]><xml><o:OfficeDocumentSettings><o:PixelsPerInch>96</o:PixelsPerInch><o:AllowPNG/></o:OfficeDocumentSettings></xml><![endif]--><!--[if !mso]><!-->\r\n"
	        + "    <link href=\"https://fonts.googleapis.com/css?family=Lato\" rel=\"stylesheet\" type=\"text/css\"><!--<![endif]-->\r\n"
	        + "    <style>\r\n" + "        * {\r\n" + "            box-sizing: border-box;\r\n" + "        }\r\n" + "\r\n"
	        + "        body {\r\n" + "            margin: 0;\r\n" + "            padding: 0;\r\n" + "        }\r\n" + "\r\n"
	        + "        p {\r\n" + "            line-height: inherit\r\n" + "        }\r\n" + "    </style>\r\n"
	        + "</head>\r\n" + "\r\n" + "<body style=\"background-color: #f5f5f5; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\r\n"
	        + "    <table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f5f5f5;\">\r\n"
	        + "        <tbody>\r\n" + "            <tr>\r\n" + "                <td>\r\n"
	        + "                    <table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
	        + "                        <tbody>\r\n" + "                            <tr>\r\n"
	        + "                                <td>\r\n"
	        + "                                    <table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\r\n"
	        + "                                        <tbody>\r\n"
	        + "                                            <tr>\r\n"
	        + "                                                <td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 5px; padding-top: 5px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
	        + "                                                    <table class=\"paragraph_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
	        + "                                                        <tr>\r\n"
	        + "                                                            <td class=\"pad\" style=\"padding-bottom:10px;padding-left:40px;padding-right:40px;\">\r\n"
	        + "                                                                <div style=\"color:#052D3D;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:12px;line-height:120%;mso-line-height-alt:25.2px;\">\r\n"
	        + "                                                                    <p style=\"margin: 0; word-break: break-word;\"><strong>RMA Number :</strong> ${rmaNumber}</p>\r\n"
	        + "                                                                    <p style=\"margin: 0; word-break: break-word;\"><strong>Line Item :</strong> ${lineItem}</p>\r\n"
	        + "																	   <p style=\\\"margin: 0; word-break: break-word;\"><strong>Invoice Number to link was :</strong> ${invoiceNumber}</p>\r\n"
	        + " 																	<p style=\\\\\\\"margin: 0; word-break: break-word;\"><strong>The Tenant for which RMA was failed :</strong> ${Tenant}</p>\r\n"				
	        + "                                                                    <p style=\"margin: 0; word-break: break-word;\">Invoice linking for the above RMA and line item has failed through the scheduler. Please manually link it.</p>\r\n"
	        + "                                                                </div>\r\n"
	        + "                                                            </td>\r\n"
	        + "                                                        </tr>\r\n"
	        + "                                                    </table>\r\n"
	        + "                                                </td>\r\n"
	        + "                                            </tr>\r\n"
	        + "                                        </tbody>\r\n" + "                                    </table>\r\n"
	        + "                                </td>\r\n" + "                            </tr>\r\n"
	        + "                        </tbody>\r\n" + "                    </table>\r\n"
	        + "                </td>\r\n" + "            </tr>\r\n" + "        </tbody>\r\n" + "    </table><!-- End -->\r\n" + "</body>\r\n" + "\r\n"
	        + "</html>";


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