package com.continuum.tenant.repos.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sidebarRMA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SidebarRMA {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private long id;

	@Column(name = "name")
	private String name;

	@Column(name = "type")
	private String type;

	@Column(name = "icon")
	private String icon;

	@Column(name = "url")
	private String url;

	@Column(name = "is_navigate")
	private boolean isNavigate;

	@ManyToOne
	@JoinColumn(name = "rma_id")
	private Page page;
}
