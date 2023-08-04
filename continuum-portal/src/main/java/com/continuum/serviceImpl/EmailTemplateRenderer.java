package com.continuum.serviceImpl;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class EmailTemplateRenderer {
	private static final String TEMPLATE_CONTENT = "<html>\n" + "<head>\n" + "    <meta charset=\"UTF-8\">\n"
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
			+ "            <h3>Return Order Summary </h3>\n" + "        </div>\n"
			+ "        <h1 class=\"gradient-text\">Hello ${order_contact_name},</h1>\n"
			+ "        <p>Your ERP Order no. is: ${order_no}</p>\n"
			+ "        <p>Your RMA Order no. is: ${rma_order_no}</p>\n"
			+ "        <p>Your RMA Order ${rma_order_no} is ${status}</p>\n"
			+ "        <h3 class=\"gradient-text\">Thank you.</h3>\n" + "    </div>\n" + "</body>\n" + "</html>";

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
			+"        <h3 class=\"gradient-text\">Thank you.</h3>\n"
			+ "    </div>\n" + "</body>\n" + "</html>";

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
}
