package com.fastcode.oidc.domain.model;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Rolepermission", schema = "sample")
@IdClass(RolepermissionId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RolepermissionEntity extends AbstractEntity {
	
//	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((permissionId == null) ? 0 : permissionId.hashCode());
//		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
//		return result;
//	}
//
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
// 
//        if (!(o instanceof RolepermissionEntity))
//            return false;
// 
//       RolepermissionEntity other = (RolepermissionEntity) o;
// 
//        return permissionId  != null &&
//        		permissionId .equals(other.getPermissionId ()) &&
//        				roleId  != null &&
//                		roleId .equals(other.getRoleId ());
//    }

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "permissionId", nullable = false)
	private Long permissionId;
	
	@Id
	@Column(name = "roleId", nullable = false)
	private Long roleId;

	@ManyToOne
	@JoinColumn(name = "permissionId", insertable=false, updatable=false)
	private PermissionEntity permission;

	@ManyToOne
	@JoinColumn(name = "roleId", insertable=false, updatable=false)
	private RoleEntity role;

	public RolepermissionEntity(Long permissionId, Long roleId) {
		super();
		this.permissionId = permissionId;
		this.roleId = roleId;
	}
	
}





