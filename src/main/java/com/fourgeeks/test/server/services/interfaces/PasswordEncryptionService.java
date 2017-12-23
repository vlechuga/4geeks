package com.fourgeeks.test.server.services.interfaces;

public interface PasswordEncryptionService {

    String encrypt(String password);

    boolean check(String plainPassword, String encryptedPassword);
}
