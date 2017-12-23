package com.fourgeeks.test.server.services;

import com.fourgeeks.test.server.services.interfaces.PasswordEncryptionService;
import com.lambdaworks.crypto.SCryptUtil;

import javax.inject.Singleton;

@Singleton
public class PasswordEncryptionServiceImpl implements PasswordEncryptionService {

    @Override
    public String encrypt(String password) {
        return SCryptUtil.scrypt(password, 16384, 8, 1);
    }

    @Override
    public boolean check(String plainPassword, String encryptedPassword) {
        return SCryptUtil.check(plainPassword, encryptedPassword);
    }

}