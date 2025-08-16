package com.fastcode.oidc.security;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.domain.irepository.IJwtRepository;
import com.fastcode.oidc.domain.model.JwtEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.fastcode.oidc.commons.error.ApiError;
import com.fastcode.oidc.commons.error.ExceptionMessageConstants;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.context.ApplicationContext;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;

import java.net.URL;

import java.text.ParseException;
import java.util.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private OIDCPropertiesConfiguration environment;
	private SecurityUtils securityUtils;
	private IUserManager _userMgr;
	private IJwtRepository jwtRepo;

	public JWTAuthorizationFilter(AuthenticationManager authManager,ApplicationContext ctx) {
		super(authManager);
		this.environment = ctx.getBean(OIDCPropertiesConfiguration.class);
		this.securityUtils = ctx.getBean(SecurityUtils.class);
		this._userMgr = ctx.getBean(IUserManager.class);
		this.jwtRepo = ctx.getBean(IJwtRepository.class);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		String authenticationToken = securityUtils.getTokenFromCookies(req.getCookies()); 
		String authorizationToken = req.getHeader(SecurityConstants.HEADER_STRING);

		if (authorizationToken == null || authenticationToken == null || !authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX) || !authenticationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(req, res);
			return;
		}

		ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED);
		AuthLoggingHelper logHelper = new AuthLoggingHelper();
		try {
			CustomAuthenticationToken customAuthentication =null;
			customAuthentication = getAuthentication(req);
			SecurityContextHolder.getContext().setAuthentication(customAuthentication);
			chain.doFilter(req, res);
			return;

		} catch (ExpiredJwtException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_EXPIRED);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		} catch (UnsupportedJwtException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_UNSUPPORTED);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		} catch (MalformedJwtException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_MALFORMED);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		} catch (SignatureException | ParseException | JOSEException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_INCORRECT_SIGNATURE);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		} catch (IllegalArgumentException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_ILLEGAL_ARGUMENT);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		} catch (JwtException exception) {
			apiError.setMessage(ExceptionMessageConstants.TOKEN_UNAUTHORIZED);
			logHelper.getLogger().error("An Exception Occurred:", exception);
			res.setStatus(401);
		}

        OutputStream out = res.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, apiError);
		out.flush();
	}
	private CustomAuthenticationToken getAuthentication(HttpServletRequest request) throws JwtException, ParseException, IOException, JOSEException {

		String authorizationToken = request.getHeader(SecurityConstants.HEADER_STRING);
		String authenticationToken =securityUtils.getTokenFromCookies(request.getCookies());
		// Check that the token is inactive in the JwtEntity table
		JwtEntity jwt = jwtRepo.findByAuthorizationTokenAndAuthenticationToken(authorizationToken,authenticationToken);

		if(jwt == null) {
			throw new JwtException("Token Does Not Exist");
		}
		Claims claims;

		if (StringUtils.isNotEmpty(authenticationToken) && authenticationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			try {
				SignedJWT accessToken = SignedJWT.parse(authenticationToken.replace(SecurityConstants.TOKEN_PREFIX, ""));
				String kid = accessToken.getHeader().getKeyID();
				JWKSet jwks = JWKSet.load(new URL(this.environment.getOidcIssuerUri() + "/discovery/v2.0/keys"));
				RSAKey jwk = (RSAKey) jwks.getKeyByKeyId(kid);
				JWSVerifier verifier = new RSASSAVerifier(jwk);
				if (accessToken.verify(verifier)) {
					System.out.println("valid signature");
				} else {
					System.out.println("invalid signature");
				}
			}catch (Exception e){
				throw e;
			}
		}

		if (StringUtils.isNotEmpty(authorizationToken) && authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			String email = null;
			String tenant = null;
			List<GrantedAuthority> authorities = null;
			claims = Jwts.parser()
					.setSigningKey(SecurityConstants.SECRET.getBytes())
					.parseClaimsJws(authorizationToken.replace(SecurityConstants.TOKEN_PREFIX, ""))
					.getBody();
			email = claims.getSubject();
			tenant = claims.get("tenant",String.class);
			List<String> scopes = claims.get("scopes", List.class);
			authorities = scopes.stream()
					.map(authority -> new SimpleGrantedAuthority(authority))
					.collect(Collectors.toList());


			if ((email != null) && StringUtils.isNotEmpty(email)) {
				return new CustomAuthenticationToken(email,null,tenant, authorities);
			}
		}
		return null;

	}

}