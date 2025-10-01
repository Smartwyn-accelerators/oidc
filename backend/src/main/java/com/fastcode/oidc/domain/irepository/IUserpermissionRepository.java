package com.fastcode.oidc.domain.irepository;

import com.fastcode.oidc.domain.model.UserpermissionId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.fastcode.oidc.domain.model.UserpermissionEntity;

@Repository
@Primary
public interface IUserpermissionRepository extends JpaRepository<UserpermissionEntity, UserpermissionId>,QuerydslPredicateExecutor<UserpermissionEntity> {
   
	 @Query("select u from UserpermissionEntity u where u.userId = ?1 and u.permissionId = ?2")
	 UserpermissionEntity findByIdAndPermissionId(Long userId, Long permissionId);
}
