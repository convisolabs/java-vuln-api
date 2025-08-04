package com.advocacia.api.services;

import org.springframework.core.io.Resource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
public class UploadImageService {
    
    private static final String DEFAULT_PICTURE_DIRECTORY = "/src/resources/frontend/assets/";
    private static final String ALLOWED_DOMAIN_PATTERN = "sitevulneravel\\.com";
    
    private final DefaultResourceLoader resourceLoader;
    
    public UploadImageService() {
        this.resourceLoader = new DefaultResourceLoader();
    }
    
    public String updateImage(@NotNull String imageUrl) throws Exception {
        if (!StringUtils.hasText(imageUrl)) {
            throw new IllegalArgumentException("URL da imagem não pode ser nula ou vazia");
        }
        
        // VULNERABILIDADE SSRF: Validação inadequada de URL
        // A validação apenas verifica se o domínio contém "sitevulneravel.com"
        // Isso permite URLs como "http://sitevulneravel.com.evil.com" ou  "http://evil.com?sitevulneravel.com" que passariam na validação
        if (isUrlValid(imageUrl)) {
            return storeImage(imageUrl);
        } else {
            throw new IllegalArgumentException("URL da imagem inválida: " + imageUrl);
        }
    }
    
    /**
     * VULNERABILIDADE SSRF: Validação de domínio inadequada
     * Esta validação é facilmente contornável e não previne ataques SSRF:
     * - Permite subdomínios maliciosos
     * - Permite URLs com parâmetros que contêm o domínio permitido
     * - Não valida o protocolo (HTTP/HTTPS)
     * - Não verifica se a URL aponta para recursos internos
     */
    private boolean isUrlValid(String imageUrl) {
        // VULNERABILIDADE SSRF: Validação inadequada que pode ser contornada
        // Exemplo de bypass: "http://evil.com?sitevulneravel.com"
        Pattern pattern = Pattern.compile(ALLOWED_DOMAIN_PATTERN);
        Matcher matcher = pattern.matcher(imageUrl);
        return matcher.find();
    }
    
    private String storeImage(String imageUrl) throws Exception {
        try {
            // VULNERABILIDADE SSRF: Faz requisição HTTP para URL externa
            // Esta linha pode ser explorada para acessar recursos internos da rede como bancos de dados, APIs internas, ou serviços de administração
            Resource resource = resourceLoader.getResource(imageUrl);
            
            String fileName = extractFileName(imageUrl);            
            Path outputPath = Paths.get(DEFAULT_PICTURE_DIRECTORY, fileName);            
            Files.createDirectories(outputPath.getParent());
            
            // VULNERABILIDADE SSRF: Copia dados de fonte externa sem validação
            // Não há verificação do tipo de arquivo, tamanho, ou conteúdo
            Files.copy(resource.getInputStream(), outputPath, StandardCopyOption.REPLACE_EXISTING);
            
            return "assets/" + fileName;
            
        } catch (IOException e) {
            throw new Exception("Erro ao baixar ou salvar imagem: " + e.getMessage(), e);
        }
    }
    
    private String extractFileName(String imageUrl) {
        String fileName = Paths.get(imageUrl).getFileName().toString();
        
        if (fileName == null || fileName.isEmpty()) {
            fileName = UUID.randomUUID().toString() + ".jpg";
        }
        
        return fileName;
    }
    
    public boolean removeImage(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return false;
        }
        
        try {
            Path filePath = Paths.get(DEFAULT_PICTURE_DIRECTORY, imagePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    public boolean imageExists(String imagePath) {
        if (!StringUtils.hasText(imagePath)) {
            return false;
        }
        
        Path filePath = Paths.get(DEFAULT_PICTURE_DIRECTORY, imagePath);
        return Files.exists(filePath);
    }
}