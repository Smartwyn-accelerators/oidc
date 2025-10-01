package com.fastcode.oidc.application.permission.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotNull;

@Getter @Setter
public class UpdatePermissionInput {

	@NotNull(message = "Id Should not be null")
    private Long id;
    
    @NotNull(message = "Display Name Should not be null")
    @Size(max = 128, message = "Display Name must be less than 128 characters")
    private String displayName;
    
    @NotNull(message = "Name Should not be null")
    @Size(max = 128, message = "Name must be less than 128 characters")
    private String name;
    
    private Long version;
}

