package com.fourgeeks.test.server.domain;

public class TokenInfo {

    private String userId;
    private String tokenType;
    private String authToken;

    public TokenInfo() {
    }

    public TokenInfo(String userId,
                     String tokenType,
                     String authToken) {
        this.userId = userId;
        this.authToken = authToken;
        this.tokenType = tokenType;
    }

    public String getUserId() {
        return userId;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getAuthToken() {
        return authToken;
    }

}
