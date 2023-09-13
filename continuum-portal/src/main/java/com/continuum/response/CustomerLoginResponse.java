package com.continuum.response;

import java.util.Date;
import java.util.List;

import com.continuum.tenant.repos.entity.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerLoginResponse {
	private String message;
    private String token;
    private Date expirationDate;
       

}
