package com.continuum.multitenant.mastertenant.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author RK
 */
@Entity
@Table(name = "tbl_tenant_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MasterTenant implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tenant_client_id")
	private Integer tenantClientId;

	@Size(max = 50)
	@Column(name = "db_name", nullable = false)
	private String dbName;

	@Size(max = 100)
	@Column(name = "url", nullable = false)
	private String url;

	@Size(max = 50)
	@Column(name = "user_name", nullable = false)
	private String userName;
	@Size(max = 100)
	@Column(name = "password", nullable = false)
	private String password;
	@Size(max = 100)
	@Column(name = "driver_class", nullable = false)
	private String driverClass;
	@Size(max = 10)
	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "subdomain")
	private String subdomain;

	@Column(name = "domain_username")
	private String domainUsername;

	@Column(name = "domain_password")
	private String domainPassword;

	@Column(name = "restocking_item_id")
	private String restockingItemId;

	@Column(name = "is_prod")
	private Boolean isProd;

	@Column(name = "email_username")
	private String emailUsername;

	@Column(name = "email_password")
	private String emailPassword;
	
	@Column(name = "default_email")
	private String defaultEmail;
	
	@Column(name = "default_password")
	private String defaultPassword;
	
	
}
