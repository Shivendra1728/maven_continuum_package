package com.continuum.repos.entity;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
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

	public ClientConfig() {
		// No-argument constructor
	}
}