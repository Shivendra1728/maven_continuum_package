package com.continuum.serviceImpl;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Templates;
import com.continuum.tenant.repos.repositories.TemplatesRepository;

@Component
public class EmailTemplateRenderer {

	private final TemplatesRepository templatesRepository;

	@Autowired
	public EmailTemplateRenderer(TemplatesRepository templatesRepository) {
		this.templatesRepository = templatesRepository;
	}

	public String getTemplateContent() {
		Templates template = templatesRepository.findById(1L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getFPASSWORD_TEMPLETE_CONTENT() {
		Templates template = templatesRepository.findById(2L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getEMAIL_LINE_ITEM_STATUS_IN_TRANSIT() {
		Templates template = templatesRepository.findById(3L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getEMAIL_NOTE_STATUS() {
		Templates template = templatesRepository.findById(4L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getAssignRMATemplate() {
		Templates template = templatesRepository.findById(5L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getCUSTMER_UNDER_REVIEW_TEMPLATE() {
		Templates template = templatesRepository.findById(6L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getVENDER_LINE_ITEM_STATUS() {
		Templates template = templatesRepository.findById(7L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getRETURN_PROCESSOR_NOTE() {
		Templates template = templatesRepository.findById(8L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getVENDER_LINE_ITEM_STATUS_CUSTOMER() {
		Templates template = templatesRepository.findById(9L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getREQ_MORE_CUST_INFO() {
		Templates template = templatesRepository.findById(10L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getDENIED_TEMPLATE() {
		Templates template = templatesRepository.findById(11L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getRMA_AUTHORIZED_TEMPLATE() {
		Templates template = templatesRepository.findById(12L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	public String getACTIVATE_ACCOUNT() {
		Templates template = templatesRepository.findById(13L).orElse(null);
		return (template != null) ? template.getTemplateContent() : null;
	}

	// Can be removed I guess.
	private static final String EMAIL_RMA_STATUS = "";

	public String getEMAIL_RMA_STATUS() {
		return EMAIL_RMA_STATUS;
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