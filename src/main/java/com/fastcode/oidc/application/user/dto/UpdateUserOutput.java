package com.fastcode.oidc.application.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class UpdateUserOutput {
	
	private Long id;
	private String emailAddress;
	
}
