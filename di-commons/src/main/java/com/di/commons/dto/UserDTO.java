package com.di.commons.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.continuum.repos.entity.BaseEntity;
import com.continuum.repos.entity.Gender;
import com.continuum.repos.entity.Permission;
import com.continuum.repos.entity.Role;

import com.continuum.repos.entity.User;
import com.continuum.repos.entity.User_Address;
import com.continuum.repos.entity.User_Contact;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Component
public class UserDTO extends BaseEntity implements Serializable {

	public UserDTO() {
		roles = new ArrayList<>();
		permissions = new ArrayList<>();
	}

	public UserDTO(User user) {
		if (user != null) {
			
			this.username = user.getUsername();
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			this.email = user.getEmail();
			this.note = user.getNote();
			
			// address, if set
			roles = new ArrayList<>();
			permissions = new ArrayList<>();

			for (Role role : user.getRoles()) {
				roles.add(role.getRole());
				for (Permission permission : role.getPermissions()) {
					String key = permission.getPermission();
					if ((!permissions.contains(key) && (permission.isEnabled()))) {
						permissions.add(key);
					}
				}
			}
		}
	}

	private String username;
	private String email;
	private boolean status;
	private String firstName;
	private String lastName;
	private String note;
	private Gender gender;
	private boolean enabled;
	private boolean secured;
	private String uuid;

	private User_Address user_Address;
	private User_Contact user_Contact;

	private List<String> roles;
	private List<String> permissions;
}