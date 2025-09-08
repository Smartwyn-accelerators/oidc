package com.fastcode.oidc.security;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import com.fastcode.oidc.domain.core.authorization.user.IUserManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.fastcode.oidc.commons.error.ApiError;
import com.fastcode.oidc.commons.error.ExceptionMessageConstants;
import com.fastcode.oidc.commons.logging.AuthLoggingHelper;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.context.ApplicationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.text.ParseException;
import java.util.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	private OIDCPropertiesConfiguration environment;
	private IUserManager _userMgr;
	private List<String> excludedPaths;
	private PathMatcher pathMatcher;

	public JWTAuthorizationFilter(AuthenticationManager authManager, ApplicationContext ctx, List<String> excludedPaths) {
		super(authManager);
		this.excludedPaths = excludedPaths; // Save the excluded paths
		this.environment = ctx.getBean(OIDCPropertiesConfiguration.class);
		this._userMgr = ctx.getBean(IUserManager.class);
		this.pathMatcher = new AntPathMatcher();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
			HttpServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		String requestPath = req.getRequestURI();
		boolean isExcluded = excludedPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
		if (isExcluded) {
			chain.doFilter(req, res);
			return;
		}

		String authorizationToken = req.getHeader(environment.getAuthHeaderString());

		if (authorizationToken == null || !authorizationToken.startsWith(environment.getAuthTokenPrefix())) {
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

		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        OutputStream out = res.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, apiError);
		out.flush();
	}
	private CustomAuthenticationToken getAuthentication(HttpServletRequest request) throws JwtException, ParseException, IOException, JOSEException {

		String authorizationToken = request.getHeader(environment.getAuthHeaderString());

		if (StringUtils.isNotEmpty(authorizationToken) && authorizationToken.startsWith(environment.getAuthTokenPrefix())) {
			String email = null;
			String tenant = null;
			List<GrantedAuthority> authorities = null;
			
			// Create secret key for JWT parsing
			SecretKey key = Keys.hmacShaKeyFor(environment.getAuthTokenSecret().getBytes());
			
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(authorizationToken.replace(environment.getAuthTokenPrefix(), ""))
					.getPayload();
			email = claims.getSubject();
			tenant = claims.get("tenant",String.class);
			List<String> scopes = claims.get("scopes", List.class);
			authorities = scopes.stream()
					.map(authority -> new SimpleGrantedAuthority(authority))
					.collect(Collectors.toList());
//			_userMgr.findByEmailAddress(email) != null additional check if needed
			if ((email != null) && StringUtils.isNotEmpty(email)) {
				return new CustomAuthenticationToken(email,null,tenant, authorities);
			}
		}
		return null;

	}

}