package com.di.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User_ContactDTO {
	private String email;
	private String phone;
	private String skype;
	private String facebook;
	private String linkedin;
	private String website;
	private String contactNote;

}
