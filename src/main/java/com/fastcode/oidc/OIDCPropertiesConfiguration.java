package com.fastcode.oidc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OIDCPropertiesConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OIDCPropertiesConfiguration.class);

    @Autowired
    private Environment env;

    private static final String OIDC_CLIENT_ID_ENV = "OIDC_CLIENT_ID";
    private static final String OIDC_CLIENT_ID_SYSPROP = "spring.security.oauth2.client.registration.oidc.client-id";

    private static final String OIDC_ISSUER_URI_ENV = "OIDC_ISSUER_URI";
    private static final String OIDC_ISSUER_URI_SYSPROP = "spring.security.oauth2.client.provider.oidc.issuer-uri";

    private static final String FASTCODE_AUTH_USER_ONLY_ENV = "FASTCODE_AUTH_USER_ONLY";
    private static final String FASTCODE_AUTH_USER_ONLY_SYSPROP = "fastCode.auth.userOnly";

    private static final String FASTCODE_AUTH_MATCHERS_PUBLIC_ENV = "FASTCODE_AUTH_MATCHERS_PUBLIC";
    private static final String FASTCODE_AUTH_MATCHERS_PUBLIC_SYSPROP = "fastCode.auth.matchers.public";

    private static final String FASTCODE_AUTH_MATCHERS_POST_ENV = "FASTCODE_AUTH_MATCHERS_POST";
    private static final String FASTCODE_AUTH_MATCHERS_POST_SYSPROP = "fastCode.auth.matchers.post";

    private static final String FASTCODE_AUTH_CORS_ALLOWED_ORIGINS_ENV = "FASTCODE_AUTH_CORS_ALLOWED_ORIGINS";
    private static final String FASTCODE_AUTH_CORS_ALLOWED_ORIGINS_SYSPROP = "fastCode.auth.cors.allowedOrigins";

    private static final String FASTCODE_AUTH_CORS_ALLOWED_METHODS_ENV = "FASTCODE_AUTH_CORS_ALLOWED_METHODS";
    private static final String FASTCODE_AUTH_CORS_ALLOWED_METHODS_SYSPROP = "fastCode.auth.cors.allowed-methods";

    private static final String FASTCODE_AUTH_CORS_ALLOWED_HEADERS_ENV = "FASTCODE_AUTH_CORS_ALLOWED_HEADERS";
    private static final String FASTCODE_AUTH_CORS_ALLOWED_HEADERS_SYSPROP = "fastCode.auth.cors.allowed-headers";
    private static final String FASTCODE_AUTH_CORS_ALLOW_CREDENTIALS_ENV = "FASTCODE_AUTH_CORS_ALLOW_CREDENTIALS";
    private static final String FASTCODE_AUTH_CORS_ALLOW_CREDENTIALS_SYSPROP = "fastCode.auth.cors.allow-credentials";

    private static final String FASTCODE_AUTH_CSRF_ENABLED_ENV = "FASTCODE_AUTH_CSRF_ENABLED";
    private static final String FASTCODE_AUTH_CSRF_ENABLED_SYSPROP = "fastCode.auth.csrf.enabled";

    private static final String FASTCODE_OFFSET_DEFAULT_ENV = "FASTCODE_OFFSET_DEFAULT";
    private static final String FASTCODE_OFFSET_DEFAULT_SYSPROP = "fastCode.offset.default";

    private static final String FASTCODE_LIMIT_DEFAULT_ENV = "FASTCODE_LIMIT_DEFAULT";
    private static final String FASTCODE_LIMIT_DEFAULT_SYSPROP = "fastCode.limit.default";

    private static final String FASTCODE_SORT_DIRECTION_DEFAULT_ENV = "FASTCODE_SORT_DIRECTION_DEFAULT";
    private static final String FASTCODE_SORT_DIRECTION_DEFAULT_SYSPROP = "fastCode.sort.direction.default";

    /**
     * @return true if only authenticated users are allowed, false otherwise
     */
    public boolean isAuthUserOnly() {
        String value = getConfigurationProperty(FASTCODE_AUTH_USER_ONLY_ENV, FASTCODE_AUTH_USER_ONLY_SYSPROP, "true");
        return Boolean.parseBoolean(value);
    }
    /**
     * @return true if only cors are allowed, false otherwise
     */
    public boolean isAllowCredentials() {
        String value = getConfigurationProperty(FASTCODE_AUTH_CORS_ALLOW_CREDENTIALS_ENV, FASTCODE_AUTH_CORS_ALLOW_CREDENTIALS_SYSPROP, "false");
        return Boolean.parseBoolean(value);
    }

    /**
     * @return true if only cors are allowed, false otherwise
     */
    public boolean isEnabledCSRF() {
        String value = getConfigurationProperty(FASTCODE_AUTH_CSRF_ENABLED_ENV, FASTCODE_AUTH_CSRF_ENABLED_SYSPROP, "false");
        return Boolean.parseBoolean(value);
    }

    /**
     * @return the public authentication matchers
     */
    public String[] getAuthMatchersPublic() {
        String value = getConfigurationProperty(FASTCODE_AUTH_MATCHERS_PUBLIC_ENV, FASTCODE_AUTH_MATCHERS_PUBLIC_SYSPROP, "/,/auth,/auth/**,/password/**,/register/**,/v2/api-docs,/actuator/**,/configuration/ui,/swagger-resources,/configuration/security,/swagger-ui.html,/webjars/**,/swagger-resources/configuration/ui,/swagger-resources/configuration/security,/browser/index.html#,/browser/**");
        return value.split(",");
    }

    /**
     * @return the post authentication matchers
     */
    public String[] getAuthMatchersPost() {
        String value = getConfigurationProperty(FASTCODE_AUTH_MATCHERS_POST_ENV, FASTCODE_AUTH_MATCHERS_POST_SYSPROP, "/register,/confirm");
        return value.split(",");
    }

    /**
     * @return the allowed CORS origins
     */
    public String[] getAuthCorsAllowedOrigins() {
        String value = getConfigurationProperty(FASTCODE_AUTH_CORS_ALLOWED_ORIGINS_ENV, FASTCODE_AUTH_CORS_ALLOWED_ORIGINS_SYSPROP, "*");
        return value.split(",");
    }

    /**
     * @return the allowed CORS methods
     */
    public String[] getAuthCorsAllowedMethods() {
        String value = getConfigurationProperty(FASTCODE_AUTH_CORS_ALLOWED_METHODS_ENV, FASTCODE_AUTH_CORS_ALLOWED_METHODS_SYSPROP, "*");
        return value.split(",");
    }


    /**
     * @return the allowed CORS headers
     */
    public String[] getAuthCorsAllowedHeaders() {
        String value = getConfigurationProperty(FASTCODE_AUTH_CORS_ALLOWED_HEADERS_ENV, FASTCODE_AUTH_CORS_ALLOWED_HEADERS_SYSPROP, "*");
        return value.split(",");
    }

    /**
     * @return the OIDC client ID
     */
    public String getOidcClientId() {
        return getConfigurationProperty(OIDC_CLIENT_ID_ENV, OIDC_CLIENT_ID_SYSPROP, "default-client-id");
    }

    /**
     * @return the OIDC issuer URI
     */
    public String getOidcIssuerUri() {
        return getConfigurationProperty(OIDC_ISSUER_URI_ENV, OIDC_ISSUER_URI_SYSPROP, "https://default-issuer.com/oauth2/default");
    }


    /**
     * @return the default offset for fastCode
     */
    public String getFastCodeOffsetDefault() {
        return getConfigurationProperty(FASTCODE_OFFSET_DEFAULT_ENV, FASTCODE_OFFSET_DEFAULT_SYSPROP, "0");
    }

    /**
     * @return the default limit for fastCode
     */
    public String getFastCodeLimitDefault() {
        return getConfigurationProperty(FASTCODE_LIMIT_DEFAULT_ENV, FASTCODE_LIMIT_DEFAULT_SYSPROP, "10");
    }

    /**
     * @return the default sort direction for fastCode
     */
    public String getFastCodeSortDirectionDefault() {
        return getConfigurationProperty(FASTCODE_SORT_DIRECTION_DEFAULT_ENV, FASTCODE_SORT_DIRECTION_DEFAULT_SYSPROP, "ASC");
    }




    /**
     * Looks for the given key in the following places (in order):
     * 1) Environment variables
     * 2) System Properties
     *
     * @param envKey
     * @param sysPropKey
     * @param defaultValue
     * @return the configured property value or default value if not found
     */
    private String getConfigurationProperty(String envKey, String sysPropKey, String defaultValue) {
        String value = env.getProperty(sysPropKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(envKey);
        }
        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }
        logger.debug("Config Property: {}/{} = {}", envKey, sysPropKey, value);
        return value;
    }
}