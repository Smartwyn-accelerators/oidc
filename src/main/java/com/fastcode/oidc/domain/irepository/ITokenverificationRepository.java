package com.fastcode.oidc.domain.irepository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import com.fastcode.oidc.domain.model.TokenverificationEntity;
import com.fastcode.oidc.domain.model.TokenverificationId;

@Repository
@Primary
public interface ITokenverificationRepository
    extends
        JpaRepository<TokenverificationEntity, TokenverificationId>,
        QuerydslPredicateExecutor<TokenverificationEntity> {
    TokenverificationEntity findByTokenAndTokenType(String token, String tokenType);

    TokenverificationEntity findByUserIdAndTokenType(Long userId, String tokenType);
}
