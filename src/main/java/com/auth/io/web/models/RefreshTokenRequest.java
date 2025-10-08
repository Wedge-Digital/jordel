package com.auth.io.web.models;

public class RefreshTokenRequest {
    private final String token;

    public String getToken() {
        return token;
    }

    // Constructeurs, getters et setters
    public RefreshTokenRequest(String token) {
        this.token = token;
    }
}