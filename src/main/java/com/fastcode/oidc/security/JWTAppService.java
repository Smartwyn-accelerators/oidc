package com.fastcode.oidc.security;

import com.fastcode.oidc.domain.irepository.IJwtRepository;
import com.fastcode.oidc.domain.model.JwtEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class JWTAppService {

	@Autowired
 	private IJwtRepository _jwtRepository;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAllUserTokens(String userName) {
	
	List<JwtEntity> userTokens = _jwtRepository.findAll();
        userTokens.removeAll(Collections.singleton(null));

        for (JwtEntity jwt : userTokens) {
            if(jwt.getUserName().equals(userName)) {
                _jwtRepository.delete(jwt);
            }
	    } 
    }
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteToken(String token) {

     JwtEntity jwt = _jwtRepository.findByAuthorizationToken(token);
     _jwtRepository.delete(jwt);

    }
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(JwtEntity jwt) {
     _jwtRepository.save(jwt);
    }
}