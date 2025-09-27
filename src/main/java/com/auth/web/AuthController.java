package com.auth.web;

import com.td.aion.io.web.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth", description = "API d'authentification des aidants via l'app mobile")
@RequestMapping("/auth")
@Profile("with-auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService service) {
        this.userService = service;
    }

    @GetMapping(value = "/me")
    @SecurityRequirement(name = "AuthJWT")
    public ResponseEntity<?> getCurrentUser () {
        return ResponseEntity.ok(this.userService.getCurrentUser());
    }

}
