package com.advocacia.api.controllers;

import com.advocacia.api.domain.user.*;
import com.advocacia.api.services.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {
    
    @Autowired
    private OAuthService oAuthService;
    
    /**
     * VULNERABILIDADE: Endpoint de autorização OAuth vulnerável
     * - Client secret exposto na URL
     * - Não valida redirect_uri
     * - Não valida scope
     * - Não valida state
     */
    @GetMapping("/authorize")
    public ResponseEntity<Object> authorize(
            @RequestParam String client_id,
            @RequestParam String client_secret, // VULNERABILIDADE: Client secret exposto
            @RequestParam String redirect_uri,
            @RequestParam String response_type,
            @RequestParam String scope,
            @RequestParam(required = false) String state) {
        
        // VULNERABILIDADE: Client secret exposto na URL
        // VULNERABILIDADE: Não valida se o client_id é válido
        // VULNERABILIDADE: Não valida se o redirect_uri é permitido
        
        try {
            // VULNERABILIDADE: Validação inadequada de client
            if (!oAuthService.validateClient(client_id, client_secret)) {
                return ResponseEntity.badRequest().body("Client inválido");
            }
            
            // VULNERABILIDADE: Gera código sem validação adequada
            String code = oAuthService.generateAuthorizationCode(client_id, redirect_uri, scope);
            
            // VULNERABILIDADE: Redirecionamento inseguro
            // Não valida se o redirect_uri é permitido
            String redirectUrl = redirect_uri + "?code=" + code;
            if (state != null) {
                redirectUrl += "&state=" + state;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("redirect_url", redirectUrl);
            response.put("code", code);
            response.put("state", state);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro na autorização: " + e.getMessage());
        }
    }
    
    @PostMapping("/token")
    public ResponseEntity<Object> token(@RequestBody OAuthTokenRequest request) {
           
        try {
            OAuthTokenResponse tokenResponse = oAuthService.exchangeCodeForToken(
                request.code(),
                request.client_id(),
                request.client_secret(),
                request.redirect_uri()
            );
            
            return ResponseEntity.ok(tokenResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro na troca de token: " + e.getMessage());
        }
    
    @GetMapping("/userinfo")
    public ResponseEntity<Object> userInfo(@RequestHeader("Authorization") String authorization) {
        
        // VULNERABILIDADE: Validação inadequada do header Authorization
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso inválido");
        }
        
        String accessToken = authorization.substring(7);
        
        try {
            OAuthUserInfo userInfo = oAuthService.getUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao obter informações do usuário: " + e.getMessage());
        }
    }
    
    /**
     * VULNERABILIDADE: Endpoint para gerar JWT a partir do OAuth vulnerável
     * - Não valida adequadamente o access token
     */
    @PostMapping("/jwt")
    public ResponseEntity<Object> generateJWT(@RequestHeader("Authorization") String authorization) {
        
        // VULNERABILIDADE: Validação inadequada do header Authorization
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token de acesso inválido");
        }
        
        String accessToken = authorization.substring(7);
        
        try {
            String jwt = oAuthService.generateJWTFromOAuth(accessToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("jwt_token", jwt);
            response.put("token_type", "Bearer");
            response.put("expires_in", "3600");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Erro ao gerar JWT: " + e.getMessage());
        }
    }
    
    /**
     * VULNERABILIDADE: Endpoint de callback OAuth vulnerável
     * - Não valida adequadamente o código
     * - Não valida o state
     */
    @GetMapping("/callback")
    public ResponseEntity<Object> callback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        
        if (error != null) {
            return ResponseEntity.badRequest().body("Erro na autorização: " + error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("state", state);
        response.put("message", "Autorização bem-sucedida");
        
        return ResponseEntity.ok(response);
    }
} 