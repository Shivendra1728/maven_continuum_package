package com.continuum.repos.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
public class User extends BaseEntity {

	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean status;

	private boolean enabled;
	private boolean secured;
	private String uuid;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "useraddressuserid")
	private User_Address user_address;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usercontactuserid")
	private User_Contact user_contact;
	
	
	@Enumerated
	@Column(columnDefinition = "tinyint")
	private Gender gender;

	

	@Column(name = "note")
	private String note;

	@Basic
	private java.time.LocalDateTime updatedDt;

	@Basic
	private java.time.LocalDateTime loginDt;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Roles> roles = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_role_id") // Change this to match your actual foreign key column name
	private Set<Roles> roles = new HashSet<>();

//
//	@ManyToOne
//	@JoinColumn(name="customerId")
//	private Customer customer;
//	
//	@OneToMany(mappedBy = "user")
//	private List<Orders> orders;
//
//	@OneToMany(mappedBy = "user")
//	private List<UserRole> userRoles;
//
//	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JoinColumn(name = "addressesId")
//	private User_Address address;
//
//	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JoinColumn(name = "contactId")
//	private Contact contact;

}
