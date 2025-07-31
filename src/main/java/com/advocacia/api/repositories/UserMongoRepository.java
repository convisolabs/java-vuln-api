package com.advocacia.api.repositories;

import com.advocacia.api.domain.user.UserMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMongoRepository extends MongoRepository<UserMongo, String> {
    
    // Método vulnerável a NoSQL Injection - aceita qualquer string como query
    @Query(value = "?0")
    List<UserMongo> findByCustomQuery(String query);
    
    // Método vulnerável a NoSQL Injection - aceita qualquer string como query
    @Query(value = "?0")
    Optional<UserMongo> findOneByCustomQuery(String query);
    
    // Método normal para buscar por login
    UserMongo findByLogin(String login);
    
    // Método normal para verificar se login existe
    boolean existsByLogin(String login);
    
    // Método normal para buscar por ID
    Optional<UserMongo> findById(String id);
    
    // Método normal para listar todos
    List<UserMongo> findAll();
} 