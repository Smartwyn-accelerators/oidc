package com.fastcode.oidc.application.userpermission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FindUserpermissionByIdOutput {

    private Long permissionId;
    private Long userId;
    private String userDescriptiveField;
  	private String permissionDescriptiveField;
  	private Boolean revoked;
  	private Long version;
  	
}
