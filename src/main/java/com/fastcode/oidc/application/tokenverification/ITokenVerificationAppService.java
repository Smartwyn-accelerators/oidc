package com.fastcode.oidc.application.tokenverification;

import com.fastcode.oidc.domain.model.TokenverificationEntity;

public interface ITokenVerificationAppService {
	
	public TokenverificationEntity findByTokenAndType(String token, String type);
	public TokenverificationEntity findByUserIdAndType(Long userId, String type);
	public TokenverificationEntity generateToken(String type, Long userId);
	public void deleteToken(TokenverificationEntity entity);

}
