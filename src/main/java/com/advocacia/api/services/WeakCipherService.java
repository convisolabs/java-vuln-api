package com.advocacia.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec; 
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.spec.AlgorithmParameterSpec;

@Service
@Component("weak-cipher")
public class WeakCipherService implements CipherService{
    
    private final static int IV_LENGTH = 8;

    private String algorithm;
    private SecretKeySpec keySpec;

    @Autowired
    public WeakCipherService(@Value("${cipher.des-key}") String key) {
       
        try {
            this.algorithm = "DES/CBC/PKCS5Padding";  
            this.keySpec = new SecretKeySpec(key.getBytes(), "DES");
                        
        } catch (Exception e) {            
            e.printStackTrace();
        }
    }
    
    @Override
    public String encrypt(String clearText) throws Exception {        
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        
        byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        
        byte[] encrypted = cipher.doFinal(clearText.getBytes());

        byte[] output = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, output, 0, iv.length);
        System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(output);
    }

    @Override
    public String decrypt(String cipherText) throws Exception {
        
        byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);

        AlgorithmParameterSpec algSpec = new IvParameterSpec(cipherTextBytes, 0, IV_LENGTH);
        
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, algSpec);        
    
        byte[] original = cipher.doFinal(cipherTextBytes, IV_LENGTH, cipherTextBytes.length - IV_LENGTH);
        
        return new String(original, "UTF-8");    
    }        
}