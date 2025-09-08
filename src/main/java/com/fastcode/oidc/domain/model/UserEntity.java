package com.fastcode.oidc.domain.model;

import jakarta.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class 	UserEntity extends AbstractEntity {

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "Id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Basic
	@Column(name = "first_name", nullable = false, length = 32)
	@NotNull
	@Size(max = 32, message = "The field must be less than 32 characters")
	private String firstName;

	@Basic
	@Column(name = "last_name", nullable = false, length = 32)
	@NotNull
	@Size(max = 32, message = "The field must be less than 32 characters")
	private String lastName;

	@Basic
	@Column(name = "email_address", nullable = false, length = 256)
	@Email
	@NotNull
	@Size(max = 256, message = "The field must be less than 256 characters")
	private String emailAddress;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<UserpermissionEntity> userpermissionSet = new HashSet<UserpermissionEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL) 
	private Set<UserroleEntity> userroleSet = new HashSet<UserroleEntity>(); 


	public void addUserpermission(UserpermissionEntity userpermission) {
		userpermissionSet.add(userpermission);
		userpermission.setUser(this);
	}

	public void removeUserpermission(UserpermissionEntity userpermission) {
		userpermissionSet.remove(userpermission);
		userpermission.setUser(null);
	}

	public void addUserrole(UserroleEntity userrole) {
		userroleSet.add(userrole);
		userrole.setUser(this);
	}

	public void removeUserrole(UserroleEntity userrole) {
		userroleSet.remove(userrole);
		userrole.setUser(null);
	}
	
	public void removeWorkspace(UserroleEntity userrole) {
		userroleSet.remove(userrole);
		userrole.setUser(null);
	}

}
