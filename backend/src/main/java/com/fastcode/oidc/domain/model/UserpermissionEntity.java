package com.fastcode.oidc.domain.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Userpermission")
@IdClass(UserpermissionId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserpermissionEntity extends AbstractEntity{
	

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((permissionId == null) ? 0 : permissionId.hashCode());
//		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
//		return result;
//	}
//
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
// 
//        if (!(o instanceof UserpermissionEntity))
//            return false;
// 
//       UserpermissionEntity other = (UserpermissionEntity) o;
// 
//        return permissionId  != null &&
//        		permissionId .equals(other.getPermissionId ()) &&
//        				userId  != null &&
//                		userId .equals(other.getUserId ());
//    }

	@Basic
	@Column(name = "revoked", nullable = true)
	private Boolean revoked;

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "permission_id", nullable = false)
	private Long permissionId;

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne
	@JoinColumn(name = "permission_id", insertable=false, updatable=false)
	private PermissionEntity permission;

	@ManyToOne
	@JoinColumn(name = "user_id", insertable=false, updatable=false)
	private UserEntity user;

}
