package com.di.commons.p21.mapper;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.commons.dto.ContactDTO;
import com.di.commons.helper.P21ContactData;
import com.di.commons.helper.P21ContactDataHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21ContactMapper {

	@Autowired
	private final ObjectMapper objectMapper;

	public P21ContactMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public ContactDTO convertP21ContactObjectToContactDTO(String contact)
			throws JsonMappingException, JsonProcessingException, ParseException {
		P21ContactDataHelper p21ContactDataHelper = objectMapper.readValue(contact, P21ContactDataHelper.class);

		ContactDTO contactDTO = new ContactDTO();
		for (P21ContactData p21ContactData : p21ContactDataHelper.getValue()) {
			contactDTO.setContactId(p21ContactData.getId());
			contactDTO.setContactEmailId(p21ContactData.getEmail_address());
			contactDTO.setContactName(p21ContactData.getContact_name());
			contactDTO.setContactPhoneNo(p21ContactData.getDirect_phone());
			contactDTO.setCustId(p21ContactData.getAddress_id());
		}

		return contactDTO;
	}

}
