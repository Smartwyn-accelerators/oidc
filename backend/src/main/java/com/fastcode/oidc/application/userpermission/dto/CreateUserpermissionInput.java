package com.fastcode.oidc.application.userpermission.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class CreateUserpermissionInput {

    @NotNull(message = "permissionId Should not be null")
    private Long permissionId;
  
    @NotNull(message = "user Id Should not be null")
    private Long userId;
    private Boolean revoked;

}
