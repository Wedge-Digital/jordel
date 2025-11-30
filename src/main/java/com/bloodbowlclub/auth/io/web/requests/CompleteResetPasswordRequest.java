package com.bloodbowlclub.auth.io.web.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class CompleteResetPasswordRequest {
    @NotNull
    private String username;
    @NotEmpty
    private String new_password;
    @NotEmpty
    private String token;
}
