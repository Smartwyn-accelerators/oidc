package com.fastcode.oidc.application.user.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class FindUserByIdOutput {

    private Long id;
    private String emailAddress;

}
