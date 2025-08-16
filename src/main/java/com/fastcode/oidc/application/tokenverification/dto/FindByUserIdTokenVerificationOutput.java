package com.fastcode.oidc.application.tokenverification.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class FindByUserIdTokenVerificationOutput {
	
	private String token;
	private Date expirationTime;
	private String tokenType;
    private Long userId;

}
