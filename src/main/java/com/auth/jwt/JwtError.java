package com.auth.jwt;

public enum JwtError {
    INVALID_SIGNATURE("Invalid JWT signature"),
    INVALID_TOKEN("Invalid JWT token"),
    EXPIRED_TOKEN("JWT token is expired"),
    UNSUPPORTED_TOKEN("JWT token is unsupported"),
    EMPTY_CLAIMS_STRING("JWT claims string is empty");

    private final String message;

    JwtError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}