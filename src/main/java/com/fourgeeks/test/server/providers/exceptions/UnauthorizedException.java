package com.fourgeeks.test.server.providers.exceptions;

public class UnauthorizedException extends RuntimeException {

    private final int code;

    public UnauthorizedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
