package com.fastcode.oidc.application.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindRoleByNameOutput {

	private Long id;
    private String displayName;
    private String name;
    
}
