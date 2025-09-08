package com.fastcode.oidc.domain.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RolepermissionId implements Serializable {

	private static final long serialVersionUID = 1L;
    private Long permissionId;
    private Long roleId;
    
	public RolepermissionId(Long permissionId, Long roleId) {
		super();
		this.permissionId = permissionId;
		this.roleId = roleId;
	}
        
}
