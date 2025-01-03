package com.skillbridgebackend.skillBridge.backend.Exception;

import org.springframework.http.HttpStatus;

public class SkillBridgeAPIException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public SkillBridgeAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
