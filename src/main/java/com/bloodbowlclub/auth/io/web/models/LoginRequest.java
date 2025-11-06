package com.bloodbowlclub.auth.io.web.models;


import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}