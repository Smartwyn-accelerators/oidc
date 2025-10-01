package com.fastcode.oidc.domain.core.authorization.userpermission;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fastcode.oidc.domain.model.UserpermissionEntity;
import com.fastcode.oidc.domain.model.UserpermissionId;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.PermissionEntity;

public interface IUserpermissionManager {
    // CRUD Operations
    public UserpermissionEntity create(UserpermissionEntity userpermission);

    void delete(UserpermissionEntity userpermission);

    public UserpermissionEntity update(UserpermissionEntity userpermission);

    public UserpermissionEntity findById(UserpermissionId userpermissionId);

    public Page<UserpermissionEntity> findAll(Predicate predicate, Pageable pageable);

    //User
    public UserEntity getUser(UserpermissionId userpermissionId);

    //Permission
    public PermissionEntity getPermission(UserpermissionId userpermissionId);
  
}
