package com.fastcode.oidc.application.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter @Setter
public class UpdateUserInput {

	@NotNull(message = "Id Should not be null")
	private Long id;

	@NotNull(message = "emailAddress Should not be null")
	@Length(max = 256, message = "emailAddress must be less than 256 characters")
	@Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message = "Email Address should be valid")
//	@Email(message = "Email Address should be valid ")
	private String emailAddress;

}
