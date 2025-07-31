package com.advocacia.api.services;

import com.advocacia.api.domain.user.UserMongo;
import com.advocacia.api.repositories.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMongoService {
    
    @Autowired
    private UserMongoRepository userMongoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Método vulnerável a NoSQL Injection
    public Optional<UserMongo> findByCustomQuery(String query) {
        return userMongoRepository.findOneByCustomQuery(query);
    }
    
    // Método vulnerável a NoSQL Injection
    public List<UserMongo> findAllByCustomQuery(String query) {
        return userMongoRepository.findByCustomQuery(query);
    }
    
    // Método normal para buscar por login
    public UserMongo findByLogin(String login) {
        return userMongoRepository.findByLogin(login);
    }
    
    // Método normal para verificar se login existe
    public boolean existsByLogin(String login) {
        return userMongoRepository.existsByLogin(login);
    }
    
    // Método normal para salvar usuário
    public UserMongo save(UserMongo user) {
        return userMongoRepository.save(user);
    }
    
    // Método normal para buscar por ID
    public Optional<UserMongo> findById(String id) {
        return userMongoRepository.findById(id);
    }
    
    // Método normal para listar todos
    public List<UserMongo> findAll() {
        return userMongoRepository.findAll();
    }
    
    // Método normal para deletar por ID
    public void deleteById(String id) {
        userMongoRepository.deleteById(id);
    }
    
    // Método normal para verificar senha
    public boolean verifyPassword(String password, UserMongo user) {
        return passwordEncoder.matches(password, user.getPassword());
    }
} 