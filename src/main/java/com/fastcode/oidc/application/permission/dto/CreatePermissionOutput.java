package com.fastcode.oidc.application.permission.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreatePermissionOutput {

    private Long id;
    private String displayName;
    private String name;
}
