package com.fastcode.oidc.domain.model;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*; 
import javax.validation.constraints.NotNull; 
import java.io.Serializable; 

@Entity
@Table(indexes =  @Index(name = "idx",columnList = "authorizationToken",unique = false),
name = "JwtEntity", schema = "sample")
@Getter @Setter
@NoArgsConstructor
public class JwtEntity implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id 
	@Column(name = "Id", nullable = false) 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 

	@Basic 
	@Column(name = "UserName", nullable = false, length = 255)
	@NotNull
	@Length(max = 255, message = "The field must be less than 255 characters")
	private String userName; 

	@Basic 
	@Column(name = "authorizationToken", nullable = false, columnDefinition="TEXT") 
	@NotNull 
//	@Length(max = 2000, message = "The field must be less than 2147483647 characters") 
	private String authorizationToken;
	
	@Basic 
	@Column(name = "authenticationToken", nullable = false, columnDefinition="TEXT") 
	@NotNull 
//	@Length(max = 2000, message = "The field must be less than 2147483647 characters") 
	private String authenticationToken;


} 