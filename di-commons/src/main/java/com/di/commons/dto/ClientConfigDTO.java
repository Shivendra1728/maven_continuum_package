package com.di.commons.dto;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.tenant.repos.entity.StoreAddress;

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
public class ClientConfigDTO {

	private Long id;
	private Integer returnPolicyPeriod;
	private boolean forceAccepTAndC;
	private String ERPConnectionString;

	private boolean notificationEnable;
	private boolean seperateDBInstance;
	private String filterSearchConfiguration;

	private String ERPDataSychInterval;
	private String feeType;

	private boolean allow_rstck_fees;
	private BigDecimal reStockingAmount;
	private String ERPCompanyId;// ERP companyId
	private String Host;
	private Integer Port;
	private String Username;
	private String Password;
	private String emailFrom;
	private String emailTO;

	private MasterTenant masterTenant;

}