package com.fastcode.oidc.restcontrollers;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.commons.domain.EmptyJsonResponse;
import com.fastcode.oidc.domain.core.authorization.role.IRoleManager;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;
import com.fastcode.oidc.domain.model.JwtEntity;
import com.fastcode.oidc.domain.model.RoleEntity;
import com.fastcode.oidc.domain.model.UserEntity;
import com.fastcode.oidc.security.JWTAppService;
import com.fastcode.oidc.security.SecurityConstants;
import com.fastcode.oidc.security.SecurityUtils;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/auth")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AuthenticationController { 

	@Autowired 
	private JWTAppService _jwtAppService;

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
	public ResponseEntity<Map<String, String>> getAuthorizationToken() {
		String idToken = utils.getTokenFromCookies(request.getCookies());
		if (idToken == null){
			throw new AccessDeniedException("ID token is empty");
		}
		String email = "";
		SignedJWT accessToken = null;
		JWTClaimsSet claimSet = null;
		try {
			accessToken = SignedJWT.parse(idToken.replace(SecurityConstants.TOKEN_PREFIX, ""));
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
		Claims claims = Jwts.claims();
		String tenant = "";

		UserEntity user = _userMgr.findByEmailAddress(email);

		if (user == null) {
			throw new UsernameNotFoundException(email);
		}

		// Set claims for JWT
		claims.setSubject(email);
		claims.put("id", user.getId());
		List<String> permissions = utils.getAllPermissionsFromUserAndRole(user);

		String[] groupsArray = new String[permissions.size()];
		List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(permissions.toArray(groupsArray));
		claims.put("scopes", (authorities.stream().map(s -> s.toString()).collect(Collectors.toList())));

		// Generate JWT token
		Date expDate = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);

		claims.setExpiration(expDate);

		String authorizationToken = Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
				.compact();

		// Save JWT to the repository
		JwtEntity jwt = new JwtEntity();
		jwt.setAuthorizationToken(SecurityConstants.TOKEN_PREFIX + authorizationToken);
		jwt.setUserName(email);
		jwt.setAuthenticationToken(idToken);
		_jwtAppService.save(jwt);

		// Set headers in response
		HttpHeaders headers = new HttpHeaders();
		headers.set("Access-Control-Allow-Credentials", "true");
		headers.add(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + authorizationToken);

		Map<String, String> tokenResponse = new HashMap<>();
		tokenResponse.put("token", SecurityConstants.TOKEN_PREFIX + authorizationToken);

		// Build and return the response entity
		return ResponseEntity.ok()
				.headers(headers)
				.contentType(MediaType.APPLICATION_JSON)
				.body(tokenResponse);
	}

	@RequestMapping(value = "/myPermissions", method = RequestMethod.GET)
	public ResponseEntity getMeInfo() throws Exception{

		String idToken = utils.getTokenFromCookies(request.getCookies());
		String email = "";
		SignedJWT accessToken = null;
		JWTClaimsSet claimSet = null;
		try {
			accessToken = SignedJWT.parse(idToken.replace(SecurityConstants.TOKEN_PREFIX, ""));
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
				UserEntity user = _userMgr.findByEmailAddress(email);
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

		String token = request.getHeader(SecurityConstants.HEADER_STRING);
		_jwtAppService.deleteToken(token);

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