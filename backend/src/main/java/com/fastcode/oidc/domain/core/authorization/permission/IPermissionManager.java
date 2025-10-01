package com.fastcode.oidc.domain.core.authorization.permission;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fastcode.oidc.domain.model.PermissionEntity;

public interface IPermissionManager {
    
    public PermissionEntity create(PermissionEntity user);

    void delete(PermissionEntity user);

    public PermissionEntity update(PermissionEntity user);

    public PermissionEntity findById(Long permissionId);

    public Page<PermissionEntity> findAll(Predicate predicate, Pageable pageable);
    
    public PermissionEntity findByPermissionName(String permissionName);
    
}
