package com.advocacia.api.domain.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String id;
    private String name;
    private String login;
    private UserRole role;
    private String password;
    private String token;

    public UserDTO(String id, String name, String login, UserRole role, String password, String token) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.role = role;
        this.password = password;
        this.token = token;
    }

    public UserDTO(String id, String name, String login, UserRole role) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.role = role;
    }
}
