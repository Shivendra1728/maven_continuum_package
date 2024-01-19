package com.di.commons.dto;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContactDTO {

	private String contactId;
	private String contactEmailId;
	private String contactPhoneNo;
	private String contactName;
	private String contactInfo;
//	private long contactNo;
	private long alternateNo;

	private String skype;
	private String note;
	private String website;
	private String linkedin;
	private String facebook;

	private String custId;
}
