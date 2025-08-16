package com.fastcode.oidc.application.userrole.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserroleOutput {

	private Long roleId;
	private Long userId;
	private String userDescriptiveField;
	private String roleDescriptiveField;

}