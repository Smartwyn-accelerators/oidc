package com.fastcode.oidc.domain.irepository;

import com.fastcode.oidc.domain.model.UserroleId;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.fastcode.oidc.domain.model.UserroleEntity;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface IUserroleRepository extends JpaRepository<UserroleEntity, UserroleId>,QuerydslPredicateExecutor<UserroleEntity> {

	@Query("select e from UserroleEntity e where e.userId = ?1 and e.roleId = ?2 ")
	UserroleEntity findByIdAndRoleId(Long userId, Long roleId);

}
