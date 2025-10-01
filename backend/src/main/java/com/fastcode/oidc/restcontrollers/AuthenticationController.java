package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.commons.domain.EmptyJsonResponse;
import com.fastcode.oidc.domain.core.authorization.role.IRoleManager;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.security.SecurityUtils;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/auth")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AuthenticationController {

	@Autowired
	private IUserManager _userMgr;

	@Autowired
	private IRoleManager _roleManager;

	@Autowired
	private SecurityUtils utils;

	@Autowired 
	private HttpServletRequest request;

	@Autowired
	private OIDCPropertiesConfiguration environment;

	@RequestMapping(value = "/getAuthorizationToken", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> getAuthorizationToken(HttpServletResponse response) {
		String idToken = utils.getTokenFromCookies(request.getCookies(), environment.getAuthHeaderAuthentication());
		if (idToken == null){
			throw new AccessDeniedException("ID token is empty");
		}
		String email = "";
		SignedJWT accessToken = null;
		JWTClaimsSet claimSet = null;
		try {
			accessToken = SignedJWT.parse(idToken.replace(environment.getAuthTokenPrefix(), ""));
			String kid = accessToken.getHeader().getKeyID();
			JWKSet jwks = null;
			jwks = JWKSet.load(new URL(this.environment.getOidcIssuerUri() + "/discovery/v2.0/keys"));
			RSAKey jwk = (RSAKey) jwks.getKeyByKeyId(kid);
			JWSVerifier verifier = new RSASSAVerifier(jwk);
			if (accessToken.verify(verifier)) {
				System.out.println("valid signature");
				claimSet = accessToken.getJWTClaimsSet();
				email = claimSet.getStringClaim("email");
			} else {
				System.out.println("invalid signature");
			}
		} catch (Exception e) {
			throw new AccessDeniedException("ID token is not valid");
		}
		UserEntity user = _userMgr.findByEmailAddress(email);

		if (user == null) {
			throw new UsernameNotFoundException(email);
		}

		// Set claims for JWT
		List<String> permissions = utils.getAllPermissionsFromUserAndRole(user);

		String[] groupsArray = new String[permissions.size()];
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(groupsArray));

		// Generate JWT token
		Date expDate = new Date(System.currentTimeMillis() + environment.getAuthTokenExpiration());

		// Create secret key for JWT signing
		SecretKey key = Keys.hmacShaKeyFor(environment.getAuthTokenSecret().getBytes());

		String authorizationToken = Jwts.builder()
				.subject(email)
				.claim("id", user.getId())
				.claim("scopes", (authorities.stream().map(s -> s.toString()).collect(Collectors.toList())))
				.expiration(expDate)
				.signWith(key)
				.compact();

		Date refreshExpDate = new Date(System.currentTimeMillis() + environment.getAuthRefreshTokenExpiration());

		// Create secret key for refresh token signing
		SecretKey refreshKey = Keys.hmacShaKeyFor(environment.getAuthRefreshTokenSecret().getBytes());

		String refreshToken = Jwts.builder()
				.subject(email)
				.expiration(refreshExpDate)
				.signWith(refreshKey)
				.compact();
		ResponseCookie refreshCookie = ResponseCookie.from(environment.getAuthHeaderRefreshToken(), environment.getAuthTokenPrefix()+refreshToken)
				.httpOnly(true)
				//.secure(true) // enable in prod
				.path("/auth/refresh")
				.maxAge(environment.getAuthRefreshTokenExpiration() / 1000)
				.sameSite("Strict")
				.build();

		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		Map<String, String> tokenResponse = new HashMap<>();
		tokenResponse.put("token", environment.getAuthTokenPrefix() + authorizationToken);

		// Build and return the response entity
		return new ResponseEntity(tokenResponse, HttpStatus.OK);
	}

	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = null;
		refreshToken = utils.getTokenFromCookies(request.getCookies(), environment.getAuthHeaderRefreshToken());
		if (refreshToken == null) {
			throw new AccessDeniedException("Refresh token missing");
		}

		try {
			refreshToken = refreshToken.replace(environment.getAuthTokenPrefix(), "");
			
			// Create secret key for refresh token parsing
			SecretKey refreshKey = Keys.hmacShaKeyFor(environment.getAuthRefreshTokenSecret().getBytes());
			
			Claims claims = Jwts.parser()
					.verifyWith(refreshKey)
					.build()
					.parseSignedClaims(refreshToken)
					.getPayload();

			String email = claims.getSubject();
			UserEntity user = _userMgr.findByEmailAddress(email);
			if (user == null) {
				throw new UsernameNotFoundException(email);
			}

			// Create new access token
			List<String> permissions = utils.getAllPermissionsFromUserAndRole(user);

			Date expDate = new Date(System.currentTimeMillis() + environment.getAuthTokenExpiration());

			// Create secret key for new access token signing
			SecretKey newKey = Keys.hmacShaKeyFor(environment.getAuthTokenSecret().getBytes());

			String newAccessToken = Jwts.builder()
					.subject(email)
					.claim("id", user.getId())
					.claim("scopes", permissions)
					.expiration(expDate)
					.signWith(newKey)
					.compact();

			if (environment.isRefreshTokenRotateEnabled()) {
				Date refreshExpDate = new Date(System.currentTimeMillis() + environment.getAuthRefreshTokenExpiration());

				String newRefreshToken = Jwts.builder()
						.subject(email)
						.expiration(refreshExpDate)
						.signWith(refreshKey)
						.compact();

				ResponseCookie refreshCookie = ResponseCookie.from(environment.getAuthHeaderRefreshToken(), environment.getAuthTokenPrefix()+newRefreshToken)
						.httpOnly(true)
						//.secure(true) // enable in prod
						.path("/auth/refresh")
						.maxAge(environment.getAuthRefreshTokenExpiration() / 1000)
						.sameSite("Strict")
						.build();
				response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
			}

			Map<String, String> tokenResponse = new HashMap<>();
			tokenResponse.put("token", environment.getAuthTokenPrefix() + newAccessToken);

			return ResponseEntity.ok(tokenResponse);

		} catch (ExpiredJwtException e) {
			throw new AccessDeniedException("Refresh token expired");
		} catch (JwtException e) {
			throw new AccessDeniedException("Invalid refresh token");
		}
	}

	@RequestMapping(value = "/myPermissions", method = RequestMethod.GET)
	public ResponseEntity getMeInfo() throws Exception{

		String idToken = utils.getTokenFromCookies(request.getCookies(), environment.getAuthHeaderAuthentication());
		String email = "";
		SignedJWT accessToken = null;
		JWTClaimsSet claimSet = null;
		try {
			accessToken = SignedJWT.parse(idToken.replace(environment.getAuthTokenPrefix(), ""));
			String kid = accessToken.getHeader().getKeyID();
			JWKSet jwks = null;
			jwks = JWKSet.load(new URL(this.environment.getOidcIssuerUri() + "/discovery/v2.0/keys"));
			RSAKey jwk = (RSAKey) jwks.getKeyByKeyId(kid);
			JWSVerifier verifier = new RSASSAVerifier(jwk);
			if (accessToken.verify(verifier)) {
				System.out.println("valid signature");
				claimSet = accessToken.getJWTClaimsSet();
				email = claimSet.getStringClaim("email");
			} else {
				System.out.println("invalid signature");
			}
		} catch (Exception e) {
			throw new EntityNotFoundException("There does not exist a user for this id token on backend");
		}
		if(environment.isAuthUserOnly()) {


			// Add all the roles and permissions in a list and then convert the list into all permissions, removing duplicates
			List<String> permissions=null;
			UserEntity user = _userMgr.findByEmailAddress(email);
			if(user !=null )
			{
				permissions = utils.getAllPermissionsFromUserAndRole(user);
			}
			else
				throw new EntityNotFoundException (
						String.format("There does not exist a user with a name=%s", email));
			return new ResponseEntity(permissions, HttpStatus.OK);}
		else {
			List<String> groups = new ArrayList<String>();
			groups = (ArrayList<String>) claimSet.getClaims().get("groups");
			List<String> permissionsList = new ArrayList<String>();
			for( String item : groups)
			{
				RoleEntity role = _roleManager.findByRoleName(item);
				if(role != null) {
					List<String> permissions= utils.getAllPermissionsFromRole(role);

					permissionsList.addAll(permissions);
				}
				else
					throw new EntityNotFoundException(
							String.format("There does not exist a role with a name=%s", item));

			}
			permissionsList= permissionsList.stream().distinct().collect(Collectors.toList());

			return new ResponseEntity(permissionsList, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST) 
	public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

		handleLogOutResponse(response);
		return new ResponseEntity(new EmptyJsonResponse(), HttpStatus.OK);
	} 

	private void handleLogOutResponse(HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals("Authentication")) {
			cookie.setMaxAge(0);
			cookie.setValue(null);
			cookie.setPath("/");
			response.addCookie(cookie);
			}
		}
	}

	@RequestMapping(method = RequestMethod.OPTIONS) 
	public ResponseEntity getCsrfToken(HttpServletRequest request) {
		return new ResponseEntity(null, HttpStatus.OK);
	}

	//    @RequestMapping("/oidc")
	//    public void securedPageOIDC(Model model, OAuth2AuthenticationToken authentication) {
	//
	//        connection.getJwtToken((List<String>) authentication.getPrincipal().getAttributes().get("groups"), (String) authentication.getPrincipal().getAttributes().get("preferred_username"));
	//
	//    }


} 
