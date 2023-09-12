package com.di.commons.dto;

import java.util.Date;

import com.continuum.tenant.repos.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRoomDTO {
	

	private String name;

	private String message;
	
	private String status;
	
	private Date followUpDate;

	private User userProfile;

}
