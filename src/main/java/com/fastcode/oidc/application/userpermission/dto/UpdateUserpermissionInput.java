package com.fastcode.oidc.application.userpermission.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
public class UpdateUserpermissionInput {

    @NotNull(message = "permissionId Should not be null")
    private Long permissionId;
  
    @NotNull(message = "user Id Should not be null")
    private Long userId;
    private Boolean revoked;    
    private Long version;
}
