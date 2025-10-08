package com.auth.io.web;

import com.auth.io.services.JwtService;
import com.auth.io.models.JwtTokens;
import com.auth.io.models.CustomUser;
import com.auth.io.web.models.LoginRequest;
import com.auth.io.web.models.RefreshTokenRequest;
import com.auth.use_cases.login.LoginCommandHandler;
import com.shared.services.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LoginCommandHandler loginHandler;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {

        Result loginResult = loginHandler.handle(loginRequest);
        if (loginResult.isFailure()) {
            return ResponseEntity.badRequest().body(loginRequest);
        }

        final JwtTokens tokens = this.jwtService.buildAuthTokens(loginRequest.getUsername());
        return ResponseEntity.ok(tokens);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        if (jwtService.validateJwtToken(token).isSuccess()) {
            String username = jwtService.getUsernameFromToken(token);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            final JwtTokens tokens = this.jwtService.buildAuthTokens(userDetails.getUsername());
//            return ResponseEntity.ok(tokens);
            return ResponseEntity.ok(null);
        } else {
            String errorMessage = jwtService.validateJwtToken(token).getError();
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<CustomUser> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
//        CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(username);
//        return ResponseEntity.ok(userDetails);
        return ResponseEntity.ok(null);
    }
}
