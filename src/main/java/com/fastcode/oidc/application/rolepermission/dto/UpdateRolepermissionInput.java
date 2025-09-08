package com.fastcode.oidc.application.rolepermission.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class UpdateRolepermissionInput {

	@NotNull(message = "permissionId Should not be null")
	private Long permissionId;

	@NotNull(message = "roleId Should not be null")
	private Long roleId;

	private Long version;

}
