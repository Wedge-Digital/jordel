package com.bloodbowlclub.auth.io.web.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterAccountRequest {
    private String username;
    private String email;
    private String password;
}
