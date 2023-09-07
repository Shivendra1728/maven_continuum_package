package com.continuum.tenant.repos.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Page {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private long id;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "admin_pages", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "admin_id"))
	private List<SidebarAdmin> admin = new ArrayList<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rma_pages", joinColumns = @JoinColumn(name = "page_id"), inverseJoinColumns = @JoinColumn(name = "rma_id"))
	private Set<SidebarRMA> rma = new HashSet();

	@ManyToMany(mappedBy = "pages")
	@JsonIgnore
	private Set<Role> roles = new HashSet<>();

}
