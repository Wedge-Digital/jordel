package com.auth.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JwtTokens {

    @JsonProperty("accessToken")
    String accessToken;

    @JsonProperty("refreshToken")
    String refreshToken;

    public JwtTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
