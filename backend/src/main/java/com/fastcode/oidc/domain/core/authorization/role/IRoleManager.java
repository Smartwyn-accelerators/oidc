package com.fastcode.oidc.domain.core.authorization.role;

import com.fastcode.oidc.domain.model.RoleEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRoleManager {

    // CRUD Operations
    public RoleEntity create(RoleEntity role);

    public void delete(RoleEntity role);

    public RoleEntity update(RoleEntity role);

    public RoleEntity findById(Long roleId);

    public RoleEntity findByRoleName(String roleName);
    
    public Page<RoleEntity> findAll(Predicate predicate, Pageable pageable);

}

