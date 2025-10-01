package com.fastcode.oidc.application.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
public class CreateUserOutput {

	private Long id;
	private String emailAddress;

}
