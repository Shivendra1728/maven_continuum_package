package com.di.integration.p21.transaction;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class P21RMAResponse {

	private String status;
	private String rmaOrderNo;
    private List<Object> messages;
}
