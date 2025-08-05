package com.advocacia.api.domain.user;

public record OAuthTokenRequest(
    String grant_type,
    String client_id,
    String client_secret,
    String code,
    String redirect_uri
) {} 