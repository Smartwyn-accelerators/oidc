package com.fastcode.oidc.domain.core.authorization.user;

import com.fastcode.oidc.domain.model.UserEntity;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserManager {
    // CRUD Operations
    public UserEntity create(UserEntity user);

    public void delete(UserEntity user);

    public UserEntity update(UserEntity user);

    public UserEntity findById(Long id);
    
    public UserEntity findByEmailAddress(String emailAddress);
    
    public Page<UserEntity> findAll(Predicate predicate, Pageable pageable);
  
}


