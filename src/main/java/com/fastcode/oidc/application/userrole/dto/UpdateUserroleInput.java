package com.fastcode.oidc.application.userrole.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class UpdateUserroleInput {

	@NotNull(message = "roleId Should not be null")
	private Long roleId;

	@NotNull(message = "user Id Should not be null")
	private Long userId;

	private Long version;

}
