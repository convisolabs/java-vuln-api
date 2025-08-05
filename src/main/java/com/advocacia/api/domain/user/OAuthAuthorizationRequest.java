package com.advocacia.api.domain.user;

public record OAuthAuthorizationRequest(
    String client_id,
    String client_secret,
    String redirect_uri,
    String response_type,
    String scope,
    String state
) {} 