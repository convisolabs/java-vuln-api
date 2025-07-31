package com.advocacia.api.infra.security;

import com.advocacia.api.domain.user.User;
import com.advocacia.api.domain.user.UserMongo;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import io.jsonwebtoken.Jwt;

@Service
public class TokenService {
    private String secret = "0123456789012345678901234567890101234567890123456789012345678901";

    public String generateToken(User user){
        try{

            return Jwts.builder()
                    .setIssuer("advocacia-api")
                    .setSubject(user.getLogin())
                    .claim("role", String.valueOf(user.getRole()))
                    .setExpiration(Date.from(genExpirationDate()))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }
    
    public String generateToken(UserMongo user){
        try{

            return Jwts.builder()
                    .setIssuer("advocacia-api")
                    .setSubject(user.getLogin())
                    .claim("role", String.valueOf(user.getRole()))
                    .setExpiration(Date.from(genExpirationDate()))
                    .signWith(SignatureAlgorithm.HS256, secret)
                    .compact();

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }

    public Authentication validateToken(String token){
        try {
            Jwt jwt = Jwts.parser()
                    .setSigningKey(secret)
                    .parse(token);

            String body = jwt.getBody().toString();


            String subject = extractValue(body, "sub=([\\w|-]+)");
            String role = extractValue(body, "role=([\\w|-]+)");

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            return new UsernamePasswordAuthenticationToken(subject, null, authorities);
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Falha ao extrair dados do token", exception);
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusMinutes(10).toInstant(ZoneOffset.of("-03:00"));
    }

    private static String extractValue(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        java.util.regex.Matcher  matcher = pattern.matcher(input);

        return matcher.find() ? matcher.group(1) : null;
    }
}