package com.fastcode.oidc.security;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private OIDCPropertiesConfiguration env;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
		String[] publicMatchers = env.getAuthMatchersPublic();
		String[] postMatchers = env.getAuthMatchersPost();
		boolean csrfEnabled = env.isEnabledCSRF();

		if (csrfEnabled) {
			http
					.cors(cors -> cors.configurationSource(corsConfigurationSource()))
					.csrf(csrf -> csrf
							.ignoringRequestMatchers(publicMatchers)
							.ignoringRequestMatchers(postMatchers)
							.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
					.sessionManagement(session -> session
							.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authorizeHttpRequests(auth -> auth
							.requestMatchers(publicMatchers).permitAll()
							.requestMatchers(HttpMethod.POST, postMatchers).permitAll()
							.anyRequest().authenticated())
					.addFilter(new JWTAuthorizationFilter(authenticationManager, context, Arrays.asList(publicMatchers)))
					.headers(headers -> headers
							.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'")));
		} else {
			http
					.cors(cors -> cors.configurationSource(corsConfigurationSource()))
					.csrf(csrf -> csrf.disable())
					.sessionManagement(session -> session
							.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authorizeHttpRequests(auth -> auth
							.requestMatchers(publicMatchers).permitAll()
							.requestMatchers(HttpMethod.POST, postMatchers).permitAll()
							.anyRequest().authenticated())
					.addFilter(new JWTAuthorizationFilter(authenticationManager, context, Arrays.asList(publicMatchers)))
					.headers(headers -> headers
							.contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'")));
		}
		return http.build();
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		List<String> allowedOrigins = Arrays.asList(env.getAuthCorsAllowedOrigins());
		List<String> allowedOriginPatterns = Arrays.asList(env.getAuthCorsAllowedOriginPatterns());
		List<String> allowedMethods = Arrays.asList(env.getAuthCorsAllowedMethods());
		List<String> allowedHeaders = Arrays.asList(env.getAuthCorsAllowedHeaders());
		boolean allowCredentials = env.isAllowCredentials();
		
		// Use allowedOriginPatterns if credentials are enabled and origins contain wildcards
		if (allowCredentials && (allowedOrigins.contains("*") || !allowedOriginPatterns.isEmpty())) {
			if (!allowedOriginPatterns.isEmpty()) {
				config.setAllowedOriginPatterns(allowedOriginPatterns);
			} else {
				// Convert wildcard to pattern for credentials
				config.setAllowedOriginPatterns(Arrays.asList("*://*:*"));
			}
		} else {
			config.setAllowedOrigins(allowedOrigins);
		}
		
		config.setAllowedMethods(allowedMethods);
		config.setAllowedHeaders(allowedHeaders);
		config.setAllowCredentials(allowCredentials);
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}