package com.di.integration.p21.transaction;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class P21RMAResponse {

	private String status;
	private String rmaOrderNo;
	private List<Object> messages;
}
