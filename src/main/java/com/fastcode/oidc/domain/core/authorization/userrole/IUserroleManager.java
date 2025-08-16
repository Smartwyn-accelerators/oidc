package com.fastcode.oidc.domain.core.authorization.userrole;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fastcode.oidc.domain.model.UserroleEntity;
import com.fastcode.oidc.domain.model.UserroleId;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.domain.model.RoleEntity;

public interface IUserroleManager {
    // CRUD Operations
    public UserroleEntity create(UserroleEntity userrole);

    void delete(UserroleEntity userrole);

    public UserroleEntity update(UserroleEntity userrole);

    public UserroleEntity findById(UserroleId userroleId);

    public Page<UserroleEntity> findAll(Predicate predicate, Pageable pageable);
   
    //User
    public UserEntity getUser(UserroleId userroleId);
   
    //Role
    public RoleEntity getRole(UserroleId userroleId);
}
