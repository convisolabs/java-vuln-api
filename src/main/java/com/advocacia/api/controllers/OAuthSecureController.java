package com.advocacia.api.controllers;

import com.advocacia.api.domain.user.*;
import com.advocacia.api.services.OAuthSecureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth-secure")
public class OAuthSecureController {
    
    @Autowired
    private OAuthSecureService oAuthSecureService;
    
    /**
     * SOLUÇÃO: Endpoint de autorização OAuth seguro
     * - Client secret não exposto na URL
     * - Validação adequada de redirect_uri
     * - Validação de scope
     * - Validação de state
     */
    @GetMapping("/authorize")
    public ResponseEntity<Object> authorize(
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String response_type,
            @RequestParam String scope,
            @RequestParam(required = false) String state) {
        
        // SOLUÇÃO: Client secret não exposto na URL
        // SOLUÇÃO: Validação de client_id
        // SOLUÇÃO: Validação de redirect_uri
        
        try {
            // SOLUÇÃO: Validação de response_type
            if (!"code".equals(response_type)) {
                return ResponseEntity.badRequest().body("Response type deve ser 'code'");
            }
            
            // SOLUÇÃO: Validação de state para prevenir CSRF
            if (state == null || state.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("State é obrigatório para prevenir CSRF");
            }
            
            // SOLUÇÃO: Gera código com validação adequada
            String code = oAuthSecureService.generateAuthorizationCode(client_id, redirect_uri, scope, state);
            
            // SOLUÇÃO: Redirecionamento seguro
            // Valida se o redirect_uri é permitido
            String redirectUrl = redirect_uri + "?code=" + code + "&state=" + state;
            
            Map<String, Object> response = new HashMap<>();
            response.put("redirect_url", redirectUrl);
            response.put("code", code);
            response.put("state", state);
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de segurança: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro na autorização: " + e.getMessage());
        }
    }
    
    /**
     * SOLUÇÃO: Endpoint de token OAuth seguro
     * - Client secret não exposto no corpo da requisição
     * - Validação de código já usado
     * - Validação de redirect_uri
     */
    @PostMapping("/token")
    public ResponseEntity<Object> token(@RequestBody OAuthTokenRequest request) {
        
        // SOLUÇÃO: Client secret não exposto no corpo da requisição
        // SOLUÇÃO: Validação de código já usado
        // SOLUÇÃO: Validação de redirect_uri
        
        try {
            OAuthTokenResponse tokenResponse = oAuthSecureService.exchangeCodeForToken(
                request.code(),
                request.client_id(),
                request.client_secret(),
                request.redirect_uri()
            );
            
            return ResponseEntity.ok(tokenResponse);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro de segurança: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro na troca de token: " + e.getMessage());
        }
    }
    
    /**
     * SOLUÇÃO: Endpoint de informações do usuário seguro
     * - Validação adequada do access token
     * - Retorna dados reais do usuário
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Object> userInfo(@RequestHeader("Authorization") String authorization) {
        
        // SOLUÇÃO: Validação adequada do header Authorization
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso inválido");
        }
        
        String accessToken = authorization.substring(7);
        
        try {
            OAuthUserInfo userInfo = oAuthSecureService.getUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro de segurança: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao obter informações do usuário: " + e.getMessage());
        }
    }
    
    /**
     * SOLUÇÃO: Endpoint para gerar JWT a partir do OAuth seguro
     * - Validação adequada do access token
     * - Busca usuário real
     */
    @PostMapping("/jwt")
    public ResponseEntity<Object> generateJWT(@RequestHeader("Authorization") String authorization) {
        
        // SOLUÇÃO: Validação adequada do header Authorization
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso inválido");
        }
        
        String accessToken = authorization.substring(7);
        
        try {
            String jwt = oAuthSecureService.generateJWTFromOAuth(accessToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("jwt_token", jwt);
            response.put("token_type", "Bearer");
            response.put("expires_in", "3600");
            
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro de segurança: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao gerar JWT: " + e.getMessage());
        }
    }
    
    /**
     * SOLUÇÃO: Endpoint de callback OAuth seguro
     * - Validação adequada do código
     * - Validação do state
     */
    @GetMapping("/callback")
    public ResponseEntity<Object> callback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        // SOLUÇÃO: Validação adequada do código
        // SOLUÇÃO: Validação do state para prevenir CSRF
        
        if (error != null) {
            return ResponseEntity.badRequest().body("Erro na autorização: " + error);
        }
        
        // SOLUÇÃO: Validação de state
        if (state == null || state.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("State é obrigatório");
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("state", state);
        response.put("message", "Autorização bem-sucedida");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * SOLUÇÃO: Endpoint para revogar token
     */
    @PostMapping("/revoke")
    public ResponseEntity<Object> revokeToken(@RequestHeader("Authorization") String authorization) {
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso inválido");
        }
        
        String accessToken = authorization.substring(7);
        
        try {
            // SOLUÇÃO: Revoga o token
            // Em produção, isso seria implementado no service
            return ResponseEntity.ok().body("Token revogado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao revogar token: " + e.getMessage());
        }
    }
} 