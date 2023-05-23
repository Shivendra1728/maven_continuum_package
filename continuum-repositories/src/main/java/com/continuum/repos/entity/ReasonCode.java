package com.continuum.repos.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
public class ReasonCode extends BaseEntity {
	/*
	 * @ManyToOne(cascade=CascadeType.ALL)
	 * 
	 * @JoinColumn(name="parentReasonCode") private ReasonCode parentReasonCode;
	 * 
	 * @OneToMany(fetch = FetchType.EAGER,mappedBy = "parentReasonCode") private
	 * List<ReasonCode> childReasonCode;
	 */
	private String code;
	private String description;
	private String status;
	
	@JsonBackReference
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "parent_reason_code_id", insertable = false, updatable = false)
	  private ReasonCode parentReasonCode;

	  @JsonManagedReference
	  @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentReasonCode")
	  private List<ReasonCode> childReasonCodes;
	
	/*
	 * @JsonBackReference
	 * 
	 * @ManyToOne(cascade=CascadeType.ALL)
	 * 
	 * @JoinColumn(name="parentDepartment") private Department parentDepartment;
	 * 
	 * 
	 * 
	 * @OneToMany(mappedBy="parentDepartment") private Set<Department>
	 * linkedDepartments = new HashSet<Department>();
	 */
	
	
	  @ManyToOne(fetch =FetchType.LAZY  )
	  @JoinColumn(name = "store_id") 
	  @JsonIgnore
	  private Store store;
	 
}
