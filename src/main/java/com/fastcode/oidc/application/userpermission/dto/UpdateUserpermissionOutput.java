package com.fastcode.oidc.application.userpermission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateUserpermissionOutput {

    private Long permissionId;
    private Long userId;
    private String userDescriptiveField;
  	private String permissionDescriptiveField;
    private Boolean revoked;
    
}
