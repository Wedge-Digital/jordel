package com.bloodbowlclub.auth.io.web.refresh_token;

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