package com.fastcode.oidc.domain.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserroleId implements Serializable {

	private static final long serialVersionUID = 1L;
    private Long roleId;
    private Long userId;
    
	public UserroleId(Long roleId, Long userId) {
		super();
		this.roleId = roleId;
		this.userId = userId;
	}
}