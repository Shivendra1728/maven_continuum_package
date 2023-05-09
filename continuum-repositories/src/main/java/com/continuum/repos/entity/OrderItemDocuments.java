package com.continuum.repos.entity;

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
public class OrderItemDocuments extends BaseEntity{

	private String URL;
	private String type;
	private String status;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="returnOrderItemId")
	private ReturnOrderItem returnOrderItem;
}
