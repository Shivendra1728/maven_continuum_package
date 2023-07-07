package com.continuum.serviceImpl;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.MethodInvocationException;

import java.io.StringWriter;

public class EmailTemplateRenderer {
    public static String renderTemplate(String templateFilePath, VelocityContext context) {
        Velocity.init();
        
        try {
            Template template = Velocity.getTemplate(templateFilePath);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            return writer.toString();
        } catch (ResourceNotFoundException | ParseErrorException | MethodInvocationException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
