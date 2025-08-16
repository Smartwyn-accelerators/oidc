package com.fastcode.oidc.application.userpermission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GetPermissionOutput {
	
   	private String displayName;
   	private Long id;
   	private String name;
   	private Long userpermissionPermissionId;
    private Long userpermissionUserId;
  
}
