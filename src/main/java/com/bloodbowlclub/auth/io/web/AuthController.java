package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.commands.LoginUserCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.domain.user_account.values.Username;
import com.bloodbowlclub.auth.io.models.CustomUser;
import com.bloodbowlclub.auth.io.models.JwtTokens;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.models.LoginRequest;
import com.bloodbowlclub.auth.io.web.models.RefreshTokenRequest;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountMapper;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountRequest;
import com.bloodbowlclub.auth.use_cases.LoginCommandHandler;
import com.bloodbowlclub.auth.use_cases.RegisterCommandHandler;
import com.bloodbowlclub.lib.services.ResultMap;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final JwtService jwtService;
    private final MessageSource messageSource;
    private final LoginCommandHandler loginHandler;
    private final RegisterCommandHandler registerHandler;
    private RegisterAccountMapper mapper = RegisterAccountMapper.INSTANCE;

    public AuthController(JwtService jwtService, MessageSource messageSource, LoginCommandHandler loginHandler, RegisterCommandHandler registerHandler) {
        this.jwtService = jwtService;
        this.messageSource = messageSource;
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {

        LoginUserCommand cmd = new LoginUserCommand(
                new Username(loginRequest.getUsername()),
                loginRequest.getUsername(),
                loginRequest.getPassword());
        ResultMap<Void> loginResult = loginHandler.handle(cmd);
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

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ResponseEntity<?> registerUser(@RequestBody RegisterAccountRequest candidateAccountRequest) {
        RegisterAccountCommand candidateAccount = mapper.requestToCommand( candidateAccountRequest );
        ResultMap<Void> registration = this.registerHandler.handle(candidateAccount);
        if (registration.isSuccess()) {
            return ResponseEntity.ok(registration.getValue());
        } else {
            return ResponseEntity.badRequest().body(registration.listErrors());
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
