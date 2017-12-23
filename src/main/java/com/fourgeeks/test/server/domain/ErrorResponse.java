package com.fourgeeks.test.server.domain;

import java.util.HashMap;
import java.util.Objects;

public class ErrorResponse {

    private int code;
    private String message;
    private String description;
    private String userDescription;
    private HashMap<String, Object> metadata;

    public ErrorResponse() {}

    public ErrorResponse(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public ErrorResponse(int code, String message, String description, String userDescription) {
        this.code = code;
        this.message = message;
        this.description = description;
        this.userDescription = userDescription;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(HashMap<String, Object> metadata) {
        this.metadata = metadata;
    }

    public ErrorResponse addMetadataValue(String key, Object value) {
        if (Objects.isNull(metadata)) metadata = new HashMap<>();
        metadata.put(key, value);
        return this;
    }
}