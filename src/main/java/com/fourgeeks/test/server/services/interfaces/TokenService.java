package com.fourgeeks.test.server.services.interfaces;

import com.fourgeeks.test.server.domain.TokenInfo;
import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.providers.exceptions.TokenException;
import com.nimbusds.jwt.SignedJWT;


public interface TokenService<T> {

    T generateToken(Person user) throws TokenException;

    String encryptToken(SignedJWT signedJWT) throws Exception;

    boolean isTokenExpired(T token) throws Exception;

    boolean verifyToken(T token) throws Exception;

    T getTokenObject(String tokenString) throws Exception;

    T decryptToken(String jweString) throws Exception;

    TokenInfo getTokenInfo(T token, boolean encrypted) throws TokenException;
}
