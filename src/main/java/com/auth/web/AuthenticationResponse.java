package com.auth.web;

import com.auth.models.JwtTokens;

public class AuthenticationResponse {
    private final JwtTokens authTokens;

    public AuthenticationResponse(JwtTokens jwt) {
        this.authTokens = jwt;
    }

    public JwtTokens getJwt() {
        return authTokens;
    }
}