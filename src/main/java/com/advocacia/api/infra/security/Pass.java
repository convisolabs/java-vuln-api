package com.advocacia.api.infra.security;

import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

public class Pass implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] encodedBytes = Base64.encode(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String encodedRawPassword = encode(rawPassword);
        return encodedRawPassword.equals(encodedPassword);
    }
}