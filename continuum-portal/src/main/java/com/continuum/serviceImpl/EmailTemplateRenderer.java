package com.continuum.serviceImpl;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;


public class EmailTemplateRenderer {
    private static final String TEMPLATE_CONTENT = "<html>\r\n"
    		+ "<head>\r\n"
    		+ "    <meta charset=\"UTF-8\">\r\n"
    		+ "    <title>Email Template</title>\r\n"
    		+ "</head>\r\n"
    		+ "<body>\r\n"
    		+ "    <h1>Hello ${order_contact_name},</h1>\r\n"
    		+ "    \r\n"
    		+ "    <p>Your ERP Order no. is: ${order_no}</p>\r\n"
    		+ "    <p>Your RMA Order no. is: ${rma_order_no}</p>\r\n"
    		+ "    \r\n"
    		+ "    <p>Your RMA Order ${rma_order_no} is ${status}</p>\r\n"
    		+ "    \r\n"
    		+ "    <p>Thank you.</p>\r\n"
    		+ "</body>\r\n"
    		+ "</html>\r\n"
    		+ "";

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
}