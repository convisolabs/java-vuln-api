package com.advocacia.api.services;

public interface CipherService {

    String encrypt(String clearText) throws Exception;
    String decrypt(String clearText) throws Exception;
    
}
