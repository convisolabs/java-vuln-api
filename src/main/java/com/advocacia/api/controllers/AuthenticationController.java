package com.advocacia.api.controllers;

import com.advocacia.api.domain.user.*;
import com.advocacia.api.infra.security.TokenService;
import com.advocacia.api.repositories.UserRepository;
import com.advocacia.api.services.IUserService;
import com.advocacia.api.services.UserMongoService;
import com.advocacia.api.domain.user.UserDTO;

import jakarta.validation.Valid;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMongoService userMongoService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        UserDetails userDetails = repository.findByLogin(data.login());

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        if (!passwordEncoder.matches(data.password(), userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha Incorreta!");
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(userDetails.getUsername(), userDetails.getAuthorities().toString(), token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().body("Login Indisponível");

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.name(), data.login(), encryptedPassword, data.role());

        this.repository.save(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", newUser.getId());
        response.put("login", newUser.getLogin());
        response.put("Role", newUser.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/updatepass/{id}")
    public ResponseEntity<Object> updatePassword(@PathVariable(value = "id") String id, @RequestBody String newPass, Authentication auth){
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isPresent()) {
            if (auth.getPrincipal().equals(existingUser.get().getLogin())) {
                existingUser.get().setPassword(passwordEncoder.encode(newPass));
                userRepository.save(existingUser.get());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Senha atualizada com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Não autorizado a atualizar essa conta.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }

    @GetMapping("/allusers")
    public ResponseEntity<Object> getAllUsers(){
        List<User> users = userService.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for(User user : users){
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getLogin(), user.getRole());
            userDTOs.add(userDTO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDTOs);
    }

    @GetMapping("/userid/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable(value = "id") String id){
        Optional<User> userId = userService.findById(id);
        return userId.<ResponseEntity<Object>>map(user -> ResponseEntity.status(HttpStatus.OK).body(user)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!"));
    }

    @DeleteMapping("/deluser/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable(value = "id") String id, Authentication auth){
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado!");
        }
        Optional<User> userId = userService.findById(id);
        if(userId.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario deletado com sucesso!");
    }
    
    // ========== ROTAS MONGODB ==========
    
    @PostMapping("/register2")
    public ResponseEntity register2(@RequestBody @Valid RegisterMongoDTO data){
        if(userMongoService.existsByLogin(data.login())) {
            return ResponseEntity.badRequest().body("Login Indisponível");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        UserMongo newUser = new UserMongo(data.name(), data.login(), encryptedPassword, data.role());

        UserMongo savedUser = userMongoService.save(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("login", savedUser.getLogin());
        response.put("role", savedUser.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login2")
    public ResponseEntity login2(@RequestBody @Valid AuthenticationMongoDTO data){
        // VULNERÁVEL A NOSQL INJECTION - aceita qualquer query como string
        // Exemplo de payload malicioso: {"login": "admin", "password": "admin' || '1' == '1"}
        String maliciousQuery = "{\"login\": \"" + data.login() + "\", \"password\": \"" + data.password() + "\"}";
        
        Optional<UserMongo> userOptional = userMongoService.findByCustomQuery(maliciousQuery);
        
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        
        UserMongo user = userOptional.get();
        
        // Verificação de senha também vulnerável
        if (!passwordEncoder.matches(data.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha Incorreta!");
        }

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginMongoResponseDTO(user.getUsername(), user.getRole().toString(), token));
    }
    
    @GetMapping("/allusers2")
    public ResponseEntity<Object> getAllUsers2(){
        List<UserMongo> users = userMongoService.findAll();
        List<Map<String, Object>> userDTOs = new ArrayList<>();
        for(UserMongo user : users){
            Map<String, Object> userDTO = new HashMap<>();
            userDTO.put("id", user.getId());
            userDTO.put("name", user.getName());
            userDTO.put("login", user.getLogin());
            userDTO.put("role", user.getRole());
            userDTOs.add(userDTO);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userDTOs);
    }
    
    @GetMapping("/userid2/{id}")
    public ResponseEntity<Object> findUserById2(@PathVariable(value = "id") String id){
        Optional<UserMongo> userId = userMongoService.findById(id);
        return userId.<ResponseEntity<Object>>map(user -> ResponseEntity.status(HttpStatus.OK).body(user)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado!"));
    }
    
    @DeleteMapping("/deluser2/{id}")
    public ResponseEntity<Object> deleteUserById2(@PathVariable(value = "id") String id, Authentication auth){
        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado!");
        }
        Optional<UserMongo> userId = userMongoService.findById(id);
        if(userId.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
        userMongoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Usuario deletado com sucesso!");
    }
}