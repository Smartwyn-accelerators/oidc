package com.fastcode.oidc.domain.irepository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import com.fastcode.oidc.domain.model.RoleEntity;

@Repository
@Primary
public interface IRoleRepository extends JpaRepository<RoleEntity, Long>, QuerydslPredicateExecutor<RoleEntity> {

	@Query("select u from RoleEntity u where u.name = ?1")
	RoleEntity findByRoleName(String value);
}