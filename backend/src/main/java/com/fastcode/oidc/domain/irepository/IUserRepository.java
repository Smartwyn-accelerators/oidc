package com.fastcode.oidc.domain.irepository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.fastcode.oidc.domain.model.UserEntity;
import org.springframework.stereotype.Repository;



@Repository
@Primary
public interface IUserRepository extends JpaRepository<UserEntity, Long>,QuerydslPredicateExecutor<UserEntity> {

    @Query("select u from UserEntity u where u.emailAddress = ?1")
    UserEntity findByEmailAddress(String emailAddress);

}
