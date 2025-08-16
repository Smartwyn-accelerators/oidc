package com.fastcode.oidc.domain.irepository;
 
import com.fastcode.oidc.domain.model.JwtEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface IJwtRepository extends JpaRepository<JwtEntity, Long> {

    JwtEntity findByAuthorizationTokenAndAuthenticationToken(String authorizationToken, String authenticationToken); 
    
    JwtEntity findByAuthorizationToken(String authorizationToken);
} 