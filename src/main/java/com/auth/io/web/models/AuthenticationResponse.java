package com.auth.io.web.models;

import com.auth.io.models.JwtTokens;

public class AuthenticationResponse {
    private final JwtTokens authTokens;

    public AuthenticationResponse(JwtTokens jwt) {
        this.authTokens = jwt;
    }

    public JwtTokens getJwt() {
        return authTokens;
    }
}