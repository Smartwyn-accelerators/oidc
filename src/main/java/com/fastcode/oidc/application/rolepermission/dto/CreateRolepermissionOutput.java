package com.fastcode.oidc.application.rolepermission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateRolepermissionOutput {

	private Long permissionId;
	private Long roleId;
	private String permissionDescriptiveField;
	private String roleDescriptiveField;

}
