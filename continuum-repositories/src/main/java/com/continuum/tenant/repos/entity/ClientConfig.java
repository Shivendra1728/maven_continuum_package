package com.continuum.tenant.repos.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor

@Component
public class ClientConfig extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "clientId")
	private Client client;

	@Builder.Default
	private boolean forceAccepTAndC = true;

	private String ERPConnectionString;

	@Builder.Default
	private boolean notificationEnable = true;

	@Builder.Default
	private boolean allow_rstck_fees = true;

	@Builder.Default
	private boolean seperateDBInstance = true;
	private String filterSearchConfiguration;
	private String ERPDataSychInterval;
	private Integer returnPolicyPeriod;
	private String feeType;
	private BigDecimal reStockingAmount;

	private String emailFrom;
	private String emailTO;
	private String erpCompanyId;

	private boolean questionsRequired;

	private String Host;
	private String Port;
	private String Username;
	private String Password;
	private String rmaQualifier;

	public ClientConfig() {
		// No-argument constructor
	}
	

	
}