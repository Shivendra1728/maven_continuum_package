package com.continuum.tenant.repos.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

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
public class ReturnRoom extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private Long id;

	private String name;

	private String message;
	
	private String status;
	
	private Date followUpDate;

	@ManyToOne
	@JoinColumn(name = "assignTo")
	private User assignTo;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_order_item_id")
    private ReturnOrderItem returnOrderItem;
}
