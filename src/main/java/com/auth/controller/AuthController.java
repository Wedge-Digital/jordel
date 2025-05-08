package com.auth.controller;

import com.auth.jwt.JwtService;
import com.auth.models.JwtTokens;
import com.auth.models.CustomUser;
import com.auth.web.AuthenticationRequest;
import com.auth.web.RefreshTokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<JwtTokens> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws BadCredentialsException {

        try {
            String username = authenticationRequest.getUsername();
            String password = authenticationRequest.getPassword();
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(userToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final JwtTokens tokens = this.jwtService.buildAuthTokens(userDetails.getUsername());

        return ResponseEntity.ok(tokens);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        if (jwtService.validateJwtToken(token).isSuccess()) {
            String username = jwtService.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            final JwtTokens tokens = this.jwtService.buildAuthTokens(userDetails.getUsername());
            return ResponseEntity.ok(tokens);
        } else {
            String errorMessage = jwtService.validateJwtToken(token).getError().getMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<CustomUser> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
        CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(userDetails);
    }
}
