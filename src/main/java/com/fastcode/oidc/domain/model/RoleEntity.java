package com.fastcode.oidc.domain.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Role")
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RoleEntity extends AbstractEntity {
	
	
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
//        if (!(o instanceof RoleEntity))
//            return false;
// 
//        RoleEntity other = (RoleEntity) o;
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
    @Column(name = "display_name", nullable = false, length = 128)
    private String displayName;
	
	@Basic
    @Column(name = "Name", nullable = false, length = 128)
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL) 
    private Set<RolepermissionEntity> rolepermissionSet = new HashSet<RolepermissionEntity>(); 
  
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL) 
    private Set<UserroleEntity> userroleSet = new HashSet<UserroleEntity>(); 
    
    public void addRolepermission(RolepermissionEntity rolepermission) {
        rolepermissionSet.add(rolepermission);
        rolepermission.setRole(this);
    }
 
    public void removeRolepermission(RolepermissionEntity rolepermission) {
    	rolepermissionSet.remove(rolepermission);
        rolepermission.setRole(null);
    }
    
    public void addUserrole(UserroleEntity userrole) {
        userroleSet.add(userrole);
        userrole.setRole(this);
    }
 
    public void removeUserrole(UserroleEntity userrole) {
    	userroleSet.remove(userrole);
        userrole.setRole(null);
    }
   
}
