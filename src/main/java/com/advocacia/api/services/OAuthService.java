package com.advocacia.api.services;

import com.advocacia.api.domain.user.*;
import com.advocacia.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthService {
    
    // VULNERABILIDADE: Client Secret hardcoded no código
    private static final String CLIENT_SECRET = "super_secret_key";
    private static final String CLIENT_ID = "advocacia_client_123";
    
    // VULNERABILIDADE: Armazenamento inseguro de códigos de autorização
    // Em produção, deveria ser armazenado em banco de dados com expiração
    private static final Map<String, String> authorizationCodes = new ConcurrentHashMap<>();
    
    // VULNERABILIDADE: Armazenamento inseguro de tokens de acesso
    // Em produção, deveria ser armazenado em banco de dados com expiração
    private static final Map<String, String> accessTokens = new ConcurrentHashMap<>();
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private IUserService userService;
    
    /**
     * VULNERABILIDADE: Validação inadequada de client_secret
     * O client_secret é comparado diretamente sem criptografia
     * e está hardcoded no código
     */
    public boolean validateClient(String clientId, String clientSecret) {
        // VULNERABILIDADE: Client secret hardcoded e exposto
        return CLIENT_ID.equals(clientId) && CLIENT_SECRET.equals(clientSecret);
    }
    
    /**
     * VULNERABILIDADE: Geração de código de autorização sem validação adequada
     * Não há validação de redirect_uri ou escopo
     */
    public String generateAuthorizationCode(String clientId, String redirectUri, String scope) {
        // VULNERABILIDADE: Não valida se o redirect_uri é permitido
        // VULNERABILIDADE: Não valida se o scope é válido
        
        String code = UUID.randomUUID().toString();
        authorizationCodes.put(code, clientId + "|" + redirectUri + "|" + scope);
        
        return code;
    }
    
    /**
     * VULNERABILIDADE: Troca de código por token sem validação adequada
     * Não valida se o código foi usado anteriormente
     */
    public OAuthTokenResponse exchangeCodeForToken(String code, String clientId, String clientSecret, String redirectUri) {
        // VULNERABILIDADE: Não valida se o código já foi usado
        // VULNERABILIDADE: Não valida se o redirect_uri corresponde ao original
        
        String storedData = authorizationCodes.get(code);
        if (storedData == null) {
            throw new RuntimeException("Código de autorização inválido");
        }
        
        // VULNERABILIDADE: Não remove o código após uso (permite reutilização)
        // authorizationCodes.remove(code);
        
        String[] parts = storedData.split("\\|");
        String originalClientId = parts[0];
        String originalRedirectUri = parts[1];
        
        // VULNERABILIDADE: Validação inadequada de redirect_uri
        if (!originalClientId.equals(clientId)) {
            throw new RuntimeException("Client ID não corresponde");
        }
        
        // VULNERABILIDADE: Não valida se redirect_uri corresponde
        // if (!originalRedirectUri.equals(redirectUri)) {
        //     throw new RuntimeException("Redirect URI não corresponde");
        // }
        
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        
        // VULNERABILIDADE: Armazenamento inseguro de tokens
        accessTokens.put(accessToken, clientId);
        
        return new OAuthTokenResponse(
            accessToken,
            "Bearer",
            refreshToken,
            parts[2],
            "3600"
        );
    }
    
    /**
     * VULNERABILIDADE: Validação inadequada de access token
     * Não verifica expiração ou revogação
     */
    public boolean validateAccessToken(String accessToken) {
        // VULNERABILIDADE: Não verifica expiração do token
        // VULNERABILIDADE: Não verifica se o token foi revogado
        return accessTokens.containsKey(accessToken);
    }
    
    /**
     * VULNERABILIDADE: Retorna informações do usuário sem validação adequada
     * Não verifica se o token tem permissão para acessar essas informações
     */
    public OAuthUserInfo getUserInfo(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new RuntimeException("Token de acesso inválido");
        }
        
        // VULNERABILIDADE: Retorna dados fixos sem verificar o usuário real
        // Em produção, deveria buscar dados do usuário baseado no token
        return new OAuthUserInfo(
            "user123",
            "Usuário OAuth",
            "user@example.com",
            "https://example.com/avatar.jpg"
        );
    }
    
    /**
     * VULNERABILIDADE: Geração de JWT sem validação adequada
     * Usa dados fixos em vez de dados reais do usuário
     */
    public String generateJWTFromOAuth(String accessToken) {
        if (!validateAccessToken(accessToken)) {
            throw new RuntimeException("Token de acesso inválido");
        }
        
        // VULNERABILIDADE: Cria usuário fake em vez de buscar usuário real
        User fakeUser = new User("OAuth User", "oauth_user", "password", UserRole.USER);
        
        return tokenService.generateToken(fakeUser);
    }
} 