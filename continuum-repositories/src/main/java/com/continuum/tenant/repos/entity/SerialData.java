package com.continuum.tenant.repos.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class SerialData extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "returnOrderItemId")
	@JsonIgnore
	private ReturnOrderItem returnOrderItem;

	private int lineNo;
	private String serialNumber;

	// Constructors, getters, setters, and other methods...
}
