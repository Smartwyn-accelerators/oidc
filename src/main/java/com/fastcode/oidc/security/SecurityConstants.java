package com.fastcode.oidc.security;

public class SecurityConstants {
    public static final String SECRET = "ApplicationSecureSecretKeyToGenerateJWTsTokenForAuthenticationAndAuthorization"; // We should place this in a secure location or an encrypted file
    public static final String REFRESH_SECRET = "ApplicationSecureSecretKeyToGenerateJWTRefreshTokenForAuthenticationAndAuthorization"; // We should place this in a secure location or an encrypted file

    public static final long EXPIRATION_TIME = 90 * 60 * 1000; // 90 minutes
    public static final String TOKEN_PREFIX = "Bearer_";
    public static final String HEADER_STRING = "Authorization";
    public static final String HEADER_STRING_AUTHENTICATION = "Authentication";

    public static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 days
    public static final String HEADER_STRING_REFRESH = "RefreshToken";
}