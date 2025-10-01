package com.fastcode.oidc.domain.core.authorization.rolepermission;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fastcode.oidc.domain.model.RolepermissionEntity;
import com.fastcode.oidc.domain.model.RolepermissionId;
import com.fastcode.oidc.domain.model.PermissionEntity;
import com.fastcode.oidc.domain.model.RoleEntity;

public interface IRolepermissionManager {
    
    public RolepermissionEntity create(RolepermissionEntity rolepermission);

    public void delete(RolepermissionEntity rolepermission);

    public RolepermissionEntity update(RolepermissionEntity rolepermission);

    public RolepermissionEntity findById(RolepermissionId rolepermissionId);

    public Page<RolepermissionEntity> findAll(Predicate predicate, Pageable pageable);
   
    //Permission
    public PermissionEntity getPermission(RolepermissionId rolepermissionId);
  
    //Role
    public RoleEntity getRole(RolepermissionId rolepermissionId);
  
}
