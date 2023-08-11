package com.continuum.tenant.repos.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * @author RK
 */
@Entity
@Getter
@Setter
public class User extends BaseEntity implements Serializable  {

 /*   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;*/

    @Size(max = 100)
    
    private String fullName;

    @Size(max = 10)
    @Column(name = "gender")
	private String gender;

    @Size(max = 50)
    @Column(name = "username",nullable = false)
    private String userName;
    @Size(max = 100)
    @Column(name = "password",nullable = false)
    private String password;
    @Size(max = 10)
    @Column(name = "status")
    private boolean status;
    @Column(name = "email",nullable = false)
    private String email;
    
    
    private String firstName;

	private String lastName;
    
    
   // private String username;

	private boolean enabled;
	private boolean secured;
	@JsonIgnore
	private String uuid;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "useraddressuserid")
	private User_Address user_address;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "usercontactuserid")
	private User_Contact user_contact;
	
	@Column(name = "note")
	private String note;

	@Basic
	private java.time.LocalDateTime updatedDt;

	@Basic
	private java.time.LocalDateTime loginDt;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Roles> roles = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_role_id") // Change this to match your actual foreign key column name
	private Set<Role> roles = new HashSet<>();


}
