package com.advocacia.api.services;

import com.advocacia.api.domain.user.User;
import com.advocacia.api.domain.user.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VulnerableUserService {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Método vulnerável a SQL Injection - concatena strings diretamente
    public User findByLoginVulnerable(String login) {
        String sql = "SELECT * FROM users WHERE login = '" + login + "'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id_user"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                
                // Converter número para enum
                String roleStr = rs.getString("role");
                UserRole role;
                if ("0".equals(roleStr)) {
                    role = UserRole.ADMIN;
                } else {
                    role = UserRole.USER;
                }
                user.setRole(role);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Método vulnerável a SQL Injection - aceita qualquer query
    public List<User> findByCustomQuery(String query) {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id_user"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                
                // Converter número para enum
                String roleStr = rs.getString("role");
                UserRole role;
                if ("0".equals(roleStr)) {
                    role = UserRole.ADMIN;
                } else {
                    role = UserRole.USER;
                }
                user.setRole(role);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Método vulnerável a SQL Injection - busca por login e senha
    public User findByLoginAndPasswordVulnerable(String login, String password) {
        String sql = "SELECT * FROM users WHERE login = '" + login + "' AND password = '" + password + "'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id_user"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                
                // Converter número para enum
                String roleStr = rs.getString("role");
                UserRole role;
                if ("0".equals(roleStr)) {
                    role = UserRole.ADMIN;
                } else {
                    role = UserRole.USER;
                }
                user.setRole(role);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Método normal para comparação
    public User findByLoginSecure(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id_user"));
                    user.setName(rs.getString("name"));
                    user.setLogin(rs.getString("login"));
                    user.setPassword(rs.getString("password"));
                    
                    // Converter número para enum
                    String roleStr = rs.getString("role");
                    UserRole role;
                    if ("0".equals(roleStr)) {
                        role = UserRole.ADMIN;
                    } else {
                        role = UserRole.USER;
                    }
                    user.setRole(role);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Método para verificar se login existe
    public boolean existsByLogin(String login) {
        String sql = "SELECT COUNT(*) FROM users WHERE login = '" + login + "'";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para listar todos os usuários
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id_user"));
                user.setName(rs.getString("name"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                
                // Converter número para enum
                String roleStr = rs.getString("role");
                UserRole role;
                if ("0".equals(roleStr)) {
                    role = UserRole.ADMIN;
                } else {
                    role = UserRole.USER;
                }
                user.setRole(role);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
} 