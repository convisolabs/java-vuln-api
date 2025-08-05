package com.advocacia.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
                .cors().and()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // VULNERABILIDADE: Ausência de Headers de Segurança HTTP
                // A aplicação não configura cabeçalhos de segurança essenciais, resultando em:
                // 1. XSS (Cross-Site Scripting) - sem X-XSS-Protection e Content-Security-Policy
                // 2. Clickjacking - sem X-Frame-Options
                // 3. MIME sniffing - sem X-Content-Type-Options
                // 4. Cache de dados sensíveis - sem Cache-Control adequado
                // 5. HSTS não configurado - sem Strict-Transport-Security
                // 6. Referrer leakage - sem Referrer-Policy
                // 7. Feature Policy não configurado - sem Permissions-Policy
                
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/allusers").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login-vulnerable").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/register2").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/login2").permitAll()
                        .requestMatchers("/api/processos/**").permitAll()
                        .requestMatchers("/oauth/**").permitAll() // VULNERABILIDADE: Endpoints OAuth abertos
                        .anyRequest().permitAll()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Pass passwordEncoder()  {
        return new Pass();
    }
}