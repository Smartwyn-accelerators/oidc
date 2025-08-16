package com.fastcode.oidc.application.rolepermission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPermissionOutput {

	private String displayName;
	private Long id;
	private String name;
	private Long rolepermissionPermissionId;
	private Long rolepermissionRoleId;
	
}
