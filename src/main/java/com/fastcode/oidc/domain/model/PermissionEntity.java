package com.fastcode.oidc.domain.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Permission")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PermissionEntity extends AbstractEntity {

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = super.hashCode();
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//
//	@Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
// 
//        if (!(o instanceof PermissionEntity))
//            return false;
// 
//        PermissionEntity other = (PermissionEntity) o;
// 
//        return id != null &&
//               id.equals(other.getId());
//    }

	@Id
	@EqualsAndHashCode.Include()
	@Column(name = "Id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Basic
	@Column(name = "name", nullable = false, length = 128, unique = true)
	private String name;

	@Basic
	@Column(name = "display_name", nullable = false, length = 128)
	private String displayName;


	@OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
	private Set<RolepermissionEntity> rolepermissionSet = new HashSet<RolepermissionEntity>(); 


	@OneToMany(mappedBy = "permission", cascade = CascadeType.ALL) 
	private Set<UserpermissionEntity> userpermissionSet = new HashSet<UserpermissionEntity>(); 

	public PermissionEntity(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}

	public void addRolepermission(RolepermissionEntity rolepermission) {
		rolepermissionSet.add(rolepermission);
		rolepermission.setPermission(this);
	}

	public void removeRolepermission(RolepermissionEntity rolepermission) {
		rolepermissionSet.remove(rolepermission);
		rolepermission.setPermission(null);
	}
	
	public void addUserpermission(UserpermissionEntity userpermission) {
		userpermissionSet.add(userpermission);
		userpermission.setPermission(this);
	}

	public void removeUserpermission(UserpermissionEntity userpermission) {
		userpermissionSet.remove(userpermission);
		userpermission.setPermission(null);
	}

}
