package com.fastcode.oidc.application.user.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter @Setter
public class UserProfile {
	
	@NotNull(message = "emailAddress Should not be null")
	@Size(max = 256, message = "emailAddress must be less than 256 characters")
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Email Address should be valid")
	private String emailAddress;
}
