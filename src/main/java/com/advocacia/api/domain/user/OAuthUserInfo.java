package com.advocacia.api.domain.user;

public record OAuthUserInfo(
    String sub,
    String name,
    String email,
    String picture
) {} 