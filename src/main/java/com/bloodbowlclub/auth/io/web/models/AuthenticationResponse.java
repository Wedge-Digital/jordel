package com.bloodbowlclub.auth.io.web.models;

import com.bloodbowlclub.auth.io.web.JwtTokensResponse;

public class AuthenticationResponse {
    private final JwtTokensResponse authTokens;

    public AuthenticationResponse(JwtTokensResponse jwt) {
        this.authTokens = jwt;
    }

    public JwtTokensResponse getJwt() {
        return authTokens;
    }
}