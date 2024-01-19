package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RmaInvoiceInfo extends BaseEntity {

	private String rmaOrderNo;
	private boolean isInvoiceLinked = false;
	private boolean isDocumentLinked = false;
	private String description;
	private Integer retryCount;
	@ManyToOne
	@JoinColumn(name = "return_order_id")
	private ReturnOrder returnOrder;

}
