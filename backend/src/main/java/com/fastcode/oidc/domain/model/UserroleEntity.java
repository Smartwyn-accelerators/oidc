package com.fastcode.oidc.domain.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Userrole")
@IdClass(UserroleId.class)
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserroleEntity extends AbstractEntity {

	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
//		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
//		return result;
//	}
//
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
// 
//        if (!(o instanceof UserroleEntity))
//            return false;
// 
//       UserroleEntity other = (UserroleEntity) o;
// 
//        return userId  != null &&
//        		userId .equals(other.getUserId ()) &&
//        				roleId  != null &&
//                		roleId .equals(other.getRoleId ());
//    }

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "role_id", nullable = false)
	private Long roleId;

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "user_id", nullable = false)
	private Long userId;


	@ManyToOne
	@JoinColumn(name = "role_id", insertable=false, updatable=false)
	private RoleEntity role;

	@ManyToOne
	@JoinColumn(name = "user_id", insertable=false, updatable=false)
	private UserEntity user;

}

  
      


