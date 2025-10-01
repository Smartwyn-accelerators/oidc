package com.fastcode.oidc.domain.model;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@MappedSuperclass
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Version
    @EqualsAndHashCode.Include()
    @Column(name = "VERSION", nullable = false)
    private Long version;

//    @Override
//    public String toString() {
//        return "AbstractEntity [version=" + version + "]";
//    }
//
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((version == null) ? 0 : version.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        AbstractEntity other = (AbstractEntity) obj;
//        
//        if (version == null) {
//            return other.version == null;
//        } else return version.equals(other.version);
//    }

}
