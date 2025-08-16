package com.fastcode.oidc.security;

import com.fastcode.oidc.OIDCPropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private OIDCPropertiesConfiguration env;

	/**
	 * This is where access to various resources (urls) in the application is
	 * defined
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String[] publicMatchers = env.getAuthMatchersPublic();
		String[] postMatchers = env.getAuthMatchersPost();
		boolean csrfEnabled = env.isEnabledCSRF();

		http
				.cors()
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests()
				.antMatchers(publicMatchers).permitAll()
				.antMatchers(HttpMethod.POST, postMatchers).permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilter(new JWTAuthorizationFilter(authenticationManager(),context))
				.headers()
				.contentSecurityPolicy("script-src 'self'");
		if (csrfEnabled) {
			http.csrf().ignoringAntMatchers(publicMatchers).ignoringAntMatchers(postMatchers).csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		} else {
			http.csrf().disable();
		}
	}
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		List<String> allowedOrigins = Arrays.asList(env.getAuthCorsAllowedOrigins());
		List<String> allowedMethods = Arrays.asList(env.getAuthCorsAllowedMethods());
		List<String> allowedHeaders = Arrays.asList(env.getAuthCorsAllowedHeaders());
		boolean allowCredentials = env.isAllowCredentials();
		config.setAllowedOrigins(allowedOrigins);
		config.setAllowedMethods(allowedMethods);
		config.setAllowedHeaders(allowedHeaders);
		config.setAllowCredentials(allowCredentials);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	/**
	 * Create an instance of the custom authentication filter which intercepts and
	 * processes the end user's login form submission for further authentication
	 * processing. This filter is added before other filters so that it can
	 * intercept the user login form submission and extract the the additional
	 * 'tenant' field
	 *
	 * @return
	 * @throws Exception
	 */

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	/**
	 * Authentication provider which provides the logged in user's credentials for
	 * verification and authentication if they are coeect
	 *
	 * @return
	 */

	public AuthenticationProvider authProvider() {
		// The custom authentication provider defined for this app
		CustomUserDetailsAuthenticationProvider provider = new CustomUserDetailsAuthenticationProvider(
				passwordEncoder(), userDetailsService);
		return provider;
	}

	@Bean(name = "passwordEncoder")
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
