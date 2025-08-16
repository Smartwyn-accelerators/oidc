package com.fastcode.oidc.domain.model;

import org.hibernate.validator.constraints.Length;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "User", schema = "sample")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserEntity extends AbstractEntity {

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "Id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Basic
	@Column(name = "EmailAddress", nullable = false, length = 256)
	@Email
	@NotNull
	@Length(max = 256, message = "The field must be less than 256 characters")
	private String emailAddress;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL) 
	private Set<UserpermissionEntity> userpermissionSet = new HashSet<UserpermissionEntity>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL) 
	private Set<UserroleEntity> userroleSet = new HashSet<UserroleEntity>(); 

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL) 
	private Set<TokenverificationEntity> tokenverificationSet = new HashSet<TokenverificationEntity>();
	
	public void addTokenVerification(TokenverificationEntity tokenVerification) {
		tokenverificationSet.add(tokenVerification);
		tokenVerification.setUser(this);
	}

	public void removeTokenVerificationEntity(TokenverificationEntity tokenVerification) {
		tokenverificationSet.remove(tokenVerification);
		tokenVerification.setUser(null);
	}

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
