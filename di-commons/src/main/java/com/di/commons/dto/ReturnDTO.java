package com.di.commons.dto;

import java.util.Date;
import java.util.List;

import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.entity.ReturnType;
import com.continuum.tenant.repos.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnDTO {
	private String rmaOrderNo;
	private Date createdDate;
	private Customer customer;
	private User user;
	private ReturnType returnType;
	private Date nextActivityDate;
	private String status;
}
