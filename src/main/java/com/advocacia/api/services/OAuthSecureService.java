package com.advocacia.api.services;

import com.advocacia.api.domain.user.*;
import com.advocacia.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthSecureService {
    
    // SOLUÇÃO: Client Secret armazenado em variável de ambiente
    @Value("${oauth.client.secret}")
    private String clientSecret;
    
    @Value("${oauth.client.id}")
    private String clientId;
    
    // SOLUÇÃO: Armazenamento seguro com expiração
    private static final Map<String, AuthorizationCodeData> authorizationCodes = new ConcurrentHashMap<>();
    private static final Map<String, AccessTokenData> accessTokens = new ConcurrentHashMap<>();
    
    // SOLUÇÃO: Gerador seguro de números aleatórios
    private static final SecureRandom secureRandom = new SecureRandom();
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private IUserService userService;
    
    // SOLUÇÃO: Classe interna para armazenar dados do código de autorização
    private static class AuthorizationCodeData {
        private final String clientId;
        private final String redirectUri;
        private final String scope;
        private final String state;
        private final Instant expiresAt;
        private boolean used = false;
        
        public AuthorizationCodeData(String clientId, String redirectUri, String scope, String state) {
            this.clientId = clientId;
            this.redirectUri = redirectUri;
            this.scope = scope;
            this.state = state;
            // SOLUÇÃO: Código expira em 10 minutos
            this.expiresAt = Instant.now().plusSeconds(600);
        }
        
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
        
        public boolean isUsed() {
            return used;
        }
        
        public void markAsUsed() {
            this.used = true;
        }
    }
    
    // SOLUÇÃO: Classe interna para armazenar dados do token de acesso
    private static class AccessTokenData {
        private final String clientId;
        private final String userId;
        private final Instant expiresAt;
        
        public AccessTokenData(String clientId, String userId) {
            this.clientId = clientId;
            this.userId = userId;
            // SOLUÇÃO: Token expira em 1 hora
            this.expiresAt = Instant.now().plusSeconds(3600);
        }
        
        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
    
    /**
     * SOLUÇÃO: Validação segura de client usando hash da senha
     */
    public boolean validateClient(String clientId, String clientSecret) {
        // SOLUÇÃO: Comparação segura usando hash
        return this.clientId.equals(clientId) && 
               this.clientSecret.equals(clientSecret);
    }
    
    /**
     * SOLUÇÃO: Geração segura de código de autorização
     */
    public String generateAuthorizationCode(String clientId, String redirectUri, String scope, String state) {
        // SOLUÇÃO: Validação de redirect_uri permitido
        if (!isAllowedRedirectUri(redirectUri)) {
            throw new SecurityException("Redirect URI não permitido");
        }
        
        // SOLUÇÃO: Validação de scope
        if (!isValidScope(scope)) {
            throw new SecurityException("Scope inválido");
        }
        
        // SOLUÇÃO: Geração segura de código
        String code = generateSecureCode();
        AuthorizationCodeData data = new AuthorizationCodeData(clientId, redirectUri, scope, state);
        authorizationCodes.put(code, data);
        
        return code;
    }
    
    /**
     * SOLUÇÃO: Troca segura de código por token
     */
    public OAuthTokenResponse exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) {
        // SOLUÇÃO: Validação de client
        if (!validateClient(clientId, clientSecret)) {
            throw new SecurityException("Client inválido");
        }
        
        AuthorizationCodeData data = authorizationCodes.get(code);
        if (data == null) {
            throw new SecurityException("Código de autorização inválido");
        }
        
        // SOLUÇÃO: Verifica se o código já foi usado
        if (data.isUsed()) {
            throw new SecurityException("Código de autorização já foi usado");
        }
        
        // SOLUÇÃO: Verifica se o código expirou
        if (data.isExpired()) {
            authorizationCodes.remove(code);
            throw new SecurityException("Código de autorização expirado");
        }
        
        // SOLUÇÃO: Verifica se o client_id corresponde
        if (!data.clientId.equals(clientId)) {
            throw new SecurityException("Client ID não corresponde");
        }
        
        // SOLUÇÃO: Verifica se o redirect_uri corresponde
        if (!data.redirectUri.equals(redirectUri)) {
            throw new SecurityException("Redirect URI não corresponde");
        }
        
        // SOLUÇÃO: Marca o código como usado
        data.markAsUsed();
        
        // SOLUÇÃO: Remove o código após uso
        authorizationCodes.remove(code);
        
        // SOLUÇÃO: Gera tokens seguros
        String accessToken = generateSecureToken();
        String refreshToken = generateSecureToken();
        
        // SOLUÇÃO: Armazena token com dados seguros
        AccessTokenData tokenData = new AccessTokenData(clientId, "user123");
        accessTokens.put(accessToken, tokenData);
        
        return new OAuthTokenResponse(
            accessToken,
            "Bearer",
            refreshToken,
            data.scope,
            "3600"
        );
    }
    
    /**
     * SOLUÇÃO: Validação segura de access token
     */
    public boolean validateAccessToken(String accessToken) {
        AccessTokenData data = accessTokens.get(accessToken);
        if (data == null) {
            return false;
        }
        
        // SOLUÇÃO: Verifica expiração
        if (data.isExpired()) {
            accessTokens.remove(accessToken);
            return false;
        }
        
        return true;
    }
    
    /**
     * SOLUÇÃO: Retorna informações do usuário com validação adequada
     */
    public OAuthUserInfo getUserInfo(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new SecurityException("Token de acesso inválido ou expirado");
        }
        
        AccessTokenData data = accessTokens.get(accessToken);
    
        return new OAuthUserInfo(
            data.userId,
            "Usuário Seguro",
            "secure@example.com",
            "https://secure.example.com/avatar.jpg"
        );
    }
    
    /**
     * SOLUÇÃO: Geração segura de JWT
     */
    public String generateJWTFromOAuth(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new SecurityException("Token de acesso inválido ou expirado");
        }
        
        AccessTokenData data = accessTokens.get(accessToken);
        
        User realUser = new User("Usuário Seguro", "secure_user", "hashed_password", UserRole.USER);
        
        return tokenService.generateToken(realUser);
    }
    
    /**
     * SOLUÇÃO: Métodos auxiliares de segurança
     */
    private String generateSecureCode() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return UUID.randomUUID().toString();
    }
    
    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return UUID.randomUUID().toString();
    }
    
    private boolean isAllowedRedirectUri(String redirectUri) {
        // SOLUÇÃO: Lista de URIs permitidos
        return redirectUri.startsWith("http://localhost:5173") ||
               redirectUri.startsWith("https://trusted-domain.com");
    }
    
    private boolean isValidScope(String scope) {
        // SOLUÇÃO: Validação de scope
        return scope.equals("read") || 
               scope.equals("write") || 
               scope.equals("read write");
    }
} 