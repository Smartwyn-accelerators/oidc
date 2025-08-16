package com.fastcode.oidc.domain.irepository;

import com.fastcode.oidc.domain.model.RolepermissionId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.fastcode.oidc.domain.model.RolepermissionEntity;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface IRolepermissionRepository extends JpaRepository<RolepermissionEntity, RolepermissionId>,QuerydslPredicateExecutor<RolepermissionEntity> {

	   @Query("select u from RolepermissionEntity u where u.roleId = ?1 and u.permissionId = ?2")
	   RolepermissionEntity findByIdAndPermissionId(Long roleId, Long permissionId);
}
