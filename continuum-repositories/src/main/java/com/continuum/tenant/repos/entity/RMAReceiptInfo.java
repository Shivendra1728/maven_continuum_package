package com.continuum.tenant.repos.entity;

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
public class RMAReceiptInfo extends BaseEntity {
	private String rmaNo;
	private String status;
	private Integer retryCount;
	private String receiptNo;
}
