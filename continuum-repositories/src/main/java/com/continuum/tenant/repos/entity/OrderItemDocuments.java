package com.continuum.tenant.repos.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class OrderItemDocuments extends BaseEntity {

	private String URL;
	private String type;
	private String status;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "returnOrderItemId")
	private ReturnOrderItem returnOrderItem;

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ReturnOrderItem getReturnOrderItem() {
		return returnOrderItem;
	}

	public void setReturnOrderItem(ReturnOrderItem returnOrderItem) {
		this.returnOrderItem = returnOrderItem;
	}
}
