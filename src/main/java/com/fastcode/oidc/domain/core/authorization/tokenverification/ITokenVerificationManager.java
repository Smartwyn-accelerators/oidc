package com.fastcode.oidc.domain.core.authorization.tokenverification;

import com.fastcode.oidc.domain.model.TokenverificationEntity;

public interface ITokenVerificationManager {
    TokenverificationEntity save(TokenverificationEntity entity);

    void delete(TokenverificationEntity entity);

    TokenverificationEntity findByTokenAndType(String token, String tokenType);

    TokenverificationEntity findByUserIdAndType(Long userId, String tokenType);
}
