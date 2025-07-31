package com.advocacia.api.domain.user;
 
public record RegisterMongoDTO(String name, String login, String password, UserRole role) {
} 