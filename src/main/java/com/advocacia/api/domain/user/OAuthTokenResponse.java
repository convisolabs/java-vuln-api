package com.advocacia.api.domain.user;

public record OAuthTokenResponse(
    String access_token,
    String token_type,
    String refresh_token,
    String scope,
    String expires_in
) {} 