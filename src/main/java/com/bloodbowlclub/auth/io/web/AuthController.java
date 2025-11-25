package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.LostLoginCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.io.models.CustomUser;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.login.LoginRequest;
import com.bloodbowlclub.auth.io.web.refresh_token.RefreshTokenRequest;
import com.bloodbowlclub.auth.io.web.requests.LostLoginRequest;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountMapper;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountRequest;
import com.bloodbowlclub.auth.use_cases.RegisterCommandHandler;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;


@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final JwtService jwtService;
    private final MessageSource messageSource;

    @Qualifier("loginCommandHandler")
    private final CommandHandler loginHandler;
    private final RegisterCommandHandler registerHandler;

    @Qualifier("lostLoginCommandHandler")
    private final CommandHandler lostLoginHandler;
    private RegisterAccountMapper mapper = RegisterAccountMapper.INSTANCE;

    public AuthController(JwtService jwtService,
                          MessageSource messageSource,
                          @Qualifier("loginCommandHandler") CommandHandler loginHandler,
                          RegisterCommandHandler registerHandler,
                          @Qualifier("lostLoginCommandHandler") CommandHandler lostLoginHandler) {
        this.jwtService = jwtService;
        this.messageSource = messageSource;
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
        this.lostLoginHandler = lostLoginHandler;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws BadCredentialsException {

        LoginCommand cmd = new LoginCommand(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        ResultMap<Void> loginResult = loginHandler.handle(cmd);
        if (loginResult.isFailure()) {
            return ResponseEntity.badRequest().body(loginResult.errorMap());
        }

        final JwtTokensResponse tokens = this.jwtService.buildAuthTokens(loginRequest.getUsername());
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
    public ResponseEntity<Map<String,String>> registerUser(@RequestBody RegisterAccountRequest candidateAccountRequest) {
        RegisterAccountCommand candidateAccount = mapper.requestToCommand( candidateAccountRequest );
        ResultMap<Void> registration = this.registerHandler.handle(candidateAccount);
        return registration.toResponse();
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<CustomUser> getCurrentUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
//        CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(username);
//        return ResponseEntity.ok(userDetails);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/lostpwd", method = RequestMethod.POST)
    public ResponseEntity<String> lostPassword(@RequestBody LostLoginRequest loginRequest) {
        LostLoginCommand cmd = new LostLoginCommand(loginRequest.getUsername());
        lostLoginHandler.handle(cmd);
        String msg = messageSource.getMessage("lostlogin.finish",null, Locale.getDefault());
        return ResponseEntity.ok(msg);
    }
}
