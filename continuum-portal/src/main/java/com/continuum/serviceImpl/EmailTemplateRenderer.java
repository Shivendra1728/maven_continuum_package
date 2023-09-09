package com.continuum.serviceImpl;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class EmailTemplateRenderer {
	private static final String TEMPLATE_CONTENT = "<html>\n" + "<head>\n" + "    <meta charset=\"UTF-8\">\n"
			+ "    <title>Email Template</title>\n" + "    <style>\n" +

			"        body {\n" + "            font-family: Arial, sans-serif;\n" + "            margin: 0;\n"
			+ "            padding: 0;\n" + "        }\n" + "        a[x-apple-data-detectors] {\n"
			+ "            color: inherit !important;\n" + "            text-decoration: inherit !important;\n"
			+ "        }\n" + "        #MessageViewBody a {\n" + "            color: inherit;\n"
			+ "            text-decoration: none;\n" + "        }\n" + "        p {\n"
			+ "            line-height: inherit\n" + "        }\n" + "        .desktop_hide,\n"
			+ "        .desktop_hide table {\n" + "            mso-hide: all;\n" + "            display: none;\n"
			+ "            max-height: 0px;\n" + "            overflow: hidden;\n" + "        }\n"
			+ "        .image_block img+div {\n" + "            display: none;\n" + "        }\n"
			+ "        @media (max-width:670px) {\n" + "            .desktop_hide table.icons-inner,\n"
			+ "            .social_block.desktop_hide .social-table {\n"
			+ "                display: inline-block !important;\n" + "            }\n" + "            .icons-inner {\n"
			+ "                text-align: center;\n" + "            }\n" + "            .icons-inner td {\n"
			+ "                margin: 0 auto;\n" + "            }\n" + "            .image_block img.fullWidth {\n"
			+ "                max-width: 100% !important;\n" + "            }\n" + "            .mobile_hide {\n"
			+ "                display: none;\n" + "            }\n" + "            .row-content {\n"
			+ "                width: 100% !important;\n" + "            }\n" + "            .stack .column {\n"
			+ "                width: 100%;\n" + "                display: block;\n" + "            }\n"
			+ "            .mobile_hide {\n" + "                min-height: 0;\n" + "                max-height: 0;\n"
			+ "                max-width: 0;\n" + "                overflow: hidden;\n"
			+ "                font-size: 0px;\n" + "            }\n" + "            .desktop_hide,\n"
			+ "            .desktop_hide table {\n" + "                display: table !important;\n"
			+ "                max-height: none !important;\n" + "            }\n" + "        }\n" + "    </style>\n"
			+ "</head>\n"
			+ "<body style=\"background-color: #f5f5f5; margin: 0; padding: 0; -webkit-text-size-adjust: none; text-size-adjust: none;\">\n"
			+ "    <table class=\"nl-container\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n"
			+ "        <tbody>\n" + "            <tr>\n" + "                <td>\n"
			+ "                    <table class=\"row row-1\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n"
			+ "                        <tbody>\n" + "                            <tr>\n"
			+ "                                <td>\n"
			+ "                                    <table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\">\n"
			+ "                                        <tbody>\n" + "                                            <tr>\n"
			+ "                                                <td class=\"column column-1\" width=\"100%\">\n"
			+ "                                                    <div class=\"spacer_block block-1\" style=\"height:30px;line-height:30px;font-size:1px;\">&#8202;</div>\n"
			+ "                                                </td>\n"
			+ "                                            </tr>\n"
			+ "                                        </tbody>\n" + "                                    </table>\n"
			+ "                                </td>\n" + "                            </tr>\n"
			+ "                        </tbody>\n" + "                    </table>\n" + "                </td>\n"
			+ "            </tr>\n" + "        </tbody>\n" + "    </table>\n" +

			"<table class=\"row row-2\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n"
			+ "<tbody>\n" + "	<tr>\n" + "		<td>\n"
			+ "			<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\n"
			+ "				<tbody>\n" + "					<tr>\n"
			+ "						<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 5px; padding-top: 5px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\n"
			+ "							<table class=\"image_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\n"
			+ "								<tr>\n"
			+ "									<td class=\"pad\" style=\"width:100%;padding-right:0px;padding-left:0px;\">\n"
			+ "										<div class=\"alignment\" align=\"center\" style=\"line-height:10px\">\n"
			+ "		<img src=\"https://8f8c0556eb.imgdist.com/public/users/Integrators/BeeProAgency/1015084_999976/1677702943611.jpeg\" style=\"display: block; height: auto; border: 0; max-width: 163px; width: 100%;\" width=\"163\" alt=\"I'm an image\" title=\"I'm an image\">\n"
			+ "      		</div>\n" + "									</td>\n"
			+ "								</tr>\n" + "							</table>\n"
			+ "						</td>\n" + "					</tr>\n" + "				</tbody>\n"
			+ "			</table>\n" + "		</td>\n" + "	  </tr>\n" + "   </tbody>\n" + "</table>\n" +

			" <table class=\"row row-3\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "						<tbody>\r\n" + "							<tr>\r\n"
			+ "								<td>\r\n"
			+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f1fff8; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\r\n"
			+ "										<tbody>\r\n"
			+ "											<tr>\r\n"
			+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 15px; padding-top: 45px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
			+ "													<table class=\"image_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:15px;padding-top:25px;width:100%;padding-right:0px;padding-left:0px;\">\r\n"
			+ "																<div class=\"alignment\" align=\"center\" style=\"line-height:10px\"><img class=\"fullWidth\" src=\"https://8f8c0556eb.imgdist.com/public/users/Integrators/BeeProAgency/1015084_999976/illustrator.png\" style=\"display: block; height: auto; border: 0; max-width: 487.5px; width: 100%;\" width=\"487.5\" alt=\"Image\" title=\"Image\"></div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\n" +

			" <table class=\"paragraph_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:5px;padding-left:15px;padding-right:10px;padding-top:20px;\">\r\n"
			+ "																<div style=\"color:#052D3D;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:38px;line-height:120%;text-align:center;mso-line-height-alt:45.6px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span style=\"color: #40b37c;\"><strong>Return Order Summary!</strong></span></p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "													<table class=\"paragraph_block block-3\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:10px;padding-left:40px;padding-right:40px;\">\r\n"
			+ "																<div style=\"color:#052D3D;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:22px;line-height:150%;text-align:center;mso-line-height-alt:33px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span><span>Hello ${order_contact_name},</span></span></p>\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span><span>Thanks for making a return order with Continuum.</span></span></p>\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span><span>We're happy to let you know that your order status is ${status}.</span></span></p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "													<table class=\"paragraph_block block-4\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:10px;padding-left:40px;padding-right:40px;padding-top:10px;\">\r\n"
			+ "																<div style=\"font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:18px;line-height:120%;text-align:center;mso-line-height-alt:21.599999999999998px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span>&nbsp;Your return order reference number is:&nbsp;<span>${rma_order_no}</span></span></p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "												</td>\r\n"
			+ "											</tr>\r\n"
			+ "										</tbody>\r\n" + "									</table>\r\n"
			+ "								</td>\r\n" + "							</tr>\r\n"
			+ "						</tbody>\r\n" + "					</table>\r\n"

			+ "					<table class=\"row row-5\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "						<tbody>\r\n" + "							<tr>\r\n"
			+ "								<td>\r\n"
			+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #f0f0f0; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\r\n"
			+ "										<tbody>\r\n"
			+ "											<tr>\r\n"
			+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; border-bottom: 18px solid #FFFFFF; border-left: 25px solid #FFFFFF; border-right: 25px solid #FFFFFF; border-top: 18px solid #FFFFFF; padding-bottom: 5px; padding-left: 35px; padding-right: 35px; padding-top: 15px; vertical-align: top;\">\r\n"
			+ "													<table class=\"paragraph_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:10px;padding-left:15px;padding-right:15px;padding-top:15px;\">\r\n"
			+ "																<div style=\"color:#052d3d;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:34px;line-height:120%;text-align:center;mso-line-height-alt:40.8px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span><strong><span><span style=\"color: #fc7318;\">Got a question?&nbsp;</span><br></span></strong><span>We're here to help you</span></span></p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "													<table class=\"paragraph_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\" style=\"padding-bottom:30px;padding-left:10px;padding-right:10px;\">\r\n"
			+ "																<div style=\"color:#787878;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:18px;line-height:150%;text-align:center;mso-line-height-alt:27px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\"><span>Send an email at &nbsp;<a href=\"mailto:info@gocontinuum.ai\" target=\"_blank\" rel=\"noopener\" title=\"info@gocontinuum.ai\" style=\"text-decoration: underline; color: #2190E3;\">info@gocontinuum.ai</a></span></p>\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\">or call us at <a href=\"tel:8337051641\" target=\"_blank\" title=\"tel:8337051641\" style=\"text-decoration: underline; color: #2190E3;\" rel=\"noopener\">(833) 705-1641</a></p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "												</td>\r\n"
			+ "											</tr>\r\n"
			+ "										</tbody>\r\n" + "									</table>\r\n"
			+ "								</td>\r\n" + "							</tr>\r\n"
			+ "						</tbody>\r\n" + "					</table>\r\n"
			+ "					<table class=\"row row-6\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "						<tbody>\r\n" + "							<tr>\r\n"
			+ "								<td>\r\n"
			+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #fff; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\r\n"
			+ "										<tbody>\r\n"
			+ "											<tr>\r\n"
			+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 5px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
			+ "													<div class=\"spacer_block block-1\" style=\"height:20px;line-height:20px;font-size:1px;\">&#8202;</div>\r\n"
			+ "												</td>\r\n"
			+ "											</tr>\r\n"
			+ "										</tbody>\r\n" + "									</table>\r\n"
			+ "								</td>\r\n" + "							</tr>\r\n"
			+ "						</tbody>\r\n" + "					</table>\r\n"
			+ "					<table class=\"row row-7\" align=\"center\" width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "						<tbody>\r\n" + "							<tr>\r\n"
			+ "								<td>\r\n"
			+ "									<table class=\"row-content stack\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; color: #000; width: 650px; margin: 0 auto;\" width=\"650\">\r\n"
			+ "										<tbody>\r\n"
			+ "											<tr>\r\n"
			+ "												<td class=\"column column-1\" width=\"100%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; font-weight: 400; text-align: left; padding-bottom: 60px; padding-top: 20px; vertical-align: top; border-top: 0px; border-right: 0px; border-bottom: 0px; border-left: 0px;\">\r\n"
			+ "													<table class=\"social_block block-1\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\">\r\n"
			+ "																<div class=\"alignment\" align=\"center\">\r\n"
			+ "																	<table class=\"social-table\" width=\"94px\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; display: inline-block;\">\r\n"
			+ "																		<tr>\r\n"
			+ "																			<td style=\"padding:0 15px 0 0px;\"><a href=\"https://www.facebook.com/people/Continuum-B2B-Returns-Process/100089723979885/\" target=\"_blank\"><img src=\"https://app-rsrc.getbee.io/public/resources/social-networks-icon-sets/circle-color/facebook@2x.png\" width=\"32\" height=\"32\" alt=\"Facebook\" title=\"Facebook\" style=\"display: block; height: auto; border: 0;\"></a></td>\r\n"
			+ "																			<td style=\"padding:0 15px 0 0px;\"><a href=\"https://www.linkedin.com/company/continuumai/\" target=\"_blank\"><img src=\"https://app-rsrc.getbee.io/public/resources/social-networks-icon-sets/circle-color/linkedin@2x.png\" width=\"32\" height=\"32\" alt=\"LinkedIn\" title=\"LinkedIn\" style=\"display: block; height: auto; border: 0;\"></a></td>\r\n"
			+ "																		</tr>\r\n"
			+ "																	</table>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "													<table class=\"paragraph_block block-2\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt; word-break: break-word;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\">\r\n"
			+ "																<div style=\"color:#555555;font-family:'Lato', Tahoma, Verdana, Segoe, sans-serif;font-size:14px;line-height:150%;text-align:center;mso-line-height-alt:21px;\">\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\">Continuum Technologies, inc</p>\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\">gocontinuum.ai</p>\r\n"
			+ "																	<p style=\"margin: 0; word-break: break-word;\">111 North Wabash Ave. Ste.100 The Garland Building Chicago, IL 60602</p>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "													<table class=\"divider_block block-3\" width=\"100%\" border=\"0\" cellpadding=\"10\" cellspacing=\"0\" role=\"presentation\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "														<tr>\r\n"
			+ "															<td class=\"pad\">\r\n"
			+ "																<div class=\"alignment\" align=\"center\">\r\n"
			+ "																	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"60%\" style=\"mso-table-lspace: 0pt; mso-table-rspace: 0pt;\">\r\n"
			+ "																		<tr>\r\n"
			+ "																			<td class=\"divider_inner\" style=\"font-size: 1px; line-height: 1px; border-top: 1px dotted #C4C4C4;\"><span>&#8202;</span></td>\r\n"
			+ "																		</tr>\r\n"
			+ "																	</table>\r\n"
			+ "																</div>\r\n"
			+ "															</td>\r\n"
			+ "														</tr>\r\n"
			+ "													</table>\r\n"
			+ "												</td>\r\n"
			+ "											</tr>\r\n"
			+ "										</tbody>\r\n" + "									</table>\r\n"
			+ "								</td>\r\n" + "							</tr>\r\n"
			+ "						</tbody>\r\n" + "					</table>\r\n" + "				</td>\r\n"
			+ "			</tr>\r\n" + "		</tbody>\r\n" + "	</table><!-- End -->\n" +

			"</body>\n" + "</html>";

	private static final String FPASSWORD_TEMPLETE_CONTENT = "<html>\n" + "<head>\n" + "    <meta charset=\"UTF-8\">\n"
			+ "    <title>Email Template</title>\n" + "    <style>\n" + "        body {\n"
			+ "            font-family: Arial, sans-serif;\n" + "            background-color: #f5f5f5;\n"
			+ "            margin: 0;\n" + "            padding: 0;\n" + "            display: flex;\n"
			+ "            justify-content: center;\n" + "            align-items: center;\n"
			+ "            min-height: 100vh;\n"
			+ "            background-image: url('https://img.freepik.com/free-vector/gradient-background-green-tones_23-2148374530.jpg');\n"
			+ "            background-size: cover;\n" + "            background-position: center;\n"
			+ "            background-repeat: no-repeat;\n" + "        }\n" + "        .email-container {\n"
			+ "            max-width: 600px;\n"
			+ "            background-image: url('https://th.bing.com/th/id/OIP.Hg2TOHknQbzGYA9cehJ_RwHaD_?w=271&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7');\n"
			+ "            border-radius: 10px;\n" + "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\n"
			+ "            padding: 5px;\n" + "            text-align: center;\n" + "            width: 100%;\n"
			+ "        }\n" + "        .logo img {\n" + "            max-width: 150px;\n"
			+ "            height: 100px;\n" + "            text-align: left;\n" + "        }\n" + "        h1 {\n"
			+ "            margin-bottom: 20px;\n" + "        }\n" + "        p {\n" + "            margin: 0 0 10px;\n"
			+ "        }\n" + "        .thank-you {\n" + "            font-style: italic;\n"
			+ "            text-align: center;\n" + "        }\n" + "        h3 {\n" + "            margin-top: 0;\n"
			+ "            color:black;\n" + "        }\n" + "        .gradient-text {\n"
			+ "            font-size: 24px;\n"
			+ "            background: linear-gradient(92.83deg, #0092B8 -8.65%, rgba(41, 166, 109, 0.8) 85.41%);\n"
			+ "            -webkit-background-clip: text;\n" + "            -webkit-text-fill-color: transparent;\n"
			+ "        }\n" + "    </style>\n" + "</head>\n" + "<body>\n" + "    <div class=\"email-container\">\n"
			+ "        <div class=\"logo\">\n"
			+ "            <img src=\"https://media.licdn.com/dms/image/C560BAQH7CHuevQWs6w/company-logo_200_200/0/1677702943611?e=1697673600&v=beta&t=gwMQwuywvhdbj2WGdjQ2-jkLuos7ZJGchVzchLIO264\" alt=\"Continuum\">\n"
			+ "            <h2>HELLO ${user_name} ,</h2>\n" + "        </div>\n"
			+ "        <h3 class=\"gradient-text\">To reset password click on the link</h3>\n"
			+ "        <p>Token : ${uuid}</p>\n" + "<a href=\"$resetUrl\">Reset Password</a>"
			+ "        <h3 class=\"gradient-text\">Thank you.</h3>\n" + "    </div>\n" + "</body>\n" + "</html>";

	private static final String EMAIL_LINE_ITEM_STATUS_IN_TRANSIT = "<h3 class=\"gradient-text\">Your status has been changed to: ${LineItemStatus},Please Update Your Tracking Code Details.</h3>\"";

	private static final String EMAIL_RMA_STATUS = "<h3 class=\"gradient-text\">Your RMA status has been changed to: ${rma_status},Please Check your RMA status.</h3>\"";

	public static String renderTemplate(VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", TEMPLATE_CONTENT);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String renderFPasswordTemplate(VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", FPASSWORD_TEMPLETE_CONTENT);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String renderStatusChangeTemplate(VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", EMAIL_LINE_ITEM_STATUS_IN_TRANSIT);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String renderRMAStatusChangeTemplate(VelocityContext context) {
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		try {
			StringWriter writer = new StringWriter();
			velocityEngine.evaluate(context, writer, "EmailTemplate", EMAIL_RMA_STATUS);
			return writer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
