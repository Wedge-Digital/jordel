package com.auth.io.web.requests;

import com.lib.use_cases.Command;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterAccountRequest {
    private String userId;
    private String username;
    private String email;
    private String password;
}
