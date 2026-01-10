package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.auth.domain.user_account.commands.CompleteResetPasswordCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.LoginCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.RegisterAccountCommand;
import com.bloodbowlclub.auth.domain.user_account.commands.StartResetPasswordCommand;
import com.bloodbowlclub.auth.io.models.CustomUser;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.login.LoginRequest;
import com.bloodbowlclub.auth.io.web.refresh_token.RefreshTokenRequest;
import com.bloodbowlclub.auth.io.web.requests.CompleteResetPasswordRequest;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountMapper;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountRequest;
import com.bloodbowlclub.auth.io.web.requests.StartResetPasswordRequest;
import com.bloodbowlclub.lib.services.result.Result;
import com.bloodbowlclub.lib.services.result.ResultMap;
import com.bloodbowlclub.lib.services.result.ResultToResponse;
import com.bloodbowlclub.lib.use_cases.CommandHandler;
import com.bloodbowlclub.lib.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth/v1")
public class AuthController {

    private final JwtService jwtService;
    private final CommandHandler loginHandler;
    private final CommandHandler registerHandler;
    private final CommandHandler startResetPasswordHandler;
    private final CommandHandler completeResetPasswordHandler;
    private final MessageSource messageSource;
    private RegisterAccountMapper mapper = RegisterAccountMapper.INSTANCE;

    private final ResultToResponse<Void> commandConverter;

    public AuthController(JwtService jwtService,
                          @Qualifier("loginCommandHandler") CommandHandler loginHandler,
                          @Qualifier("registerCommandHandler") CommandHandler registerHandler,
                          @Qualifier("startResetPasswordCommandHandler") CommandHandler startResetPasswordHandler,
                          @Qualifier("completeResetPasswordCommandHandler") CommandHandler completeResetPasswordHandler,
                          MessageSource messageSource
    ) {
        this.jwtService = jwtService;
        this.loginHandler = loginHandler;
        this.registerHandler = registerHandler;
        this.startResetPasswordHandler = startResetPasswordHandler;
        this.completeResetPasswordHandler = completeResetPasswordHandler;
        this.messageSource = messageSource;
        this.commandConverter = new ResultToResponse<>(messageSource);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) throws BadCredentialsException {

        LoginCommand cmd = new LoginCommand(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        ResultMap<Void> loginResult = loginHandler.handle(cmd);
        if (loginResult.isFailure()) {
            // Resolve translatable messages to localized strings
            return ResponseEntity.badRequest().body(
                loginResult.getTranslatedErrorMap(messageSource, LocaleContextHolder.getLocale())
            );
        }

        final JwtTokensResponse tokens = this.jwtService.buildAuthTokens(loginRequest.getUsername());
        return ResponseEntity.ok(tokens);
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        Result<String> validationResult = jwtService.validateJwtToken(token);
        if (validationResult.isSuccess()) {
            String username = jwtService.getUsernameFromToken(token);
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            final JwtTokens tokens = this.jwtService.buildAuthTokens(userDetails.getUsername());
//            return ResponseEntity.ok(tokens);
            return ResponseEntity.ok(null);
        } else {
            // Resolve translatable message to localized string
            String errorMessage = validationResult.getTranslatedError(messageSource, LocaleContextHolder.getLocale());
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody RegisterAccountRequest candidateAccountRequest) {
        RegisterAccountCommand candidateAccount = mapper.requestToCommand( candidateAccountRequest );
        ResultMap<Void> registration = this.registerHandler.handle(candidateAccount);
        return commandConverter.toResponse(registration);
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public ResponseEntity<CustomUser> getCurrentUser(@Valid @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
//        CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(username);
//        return ResponseEntity.ok(userDetails);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/start-reset-password", method = RequestMethod.POST)
    public ResponseEntity<Void> startResetPassword(@Valid @RequestBody StartResetPasswordRequest loginRequest) {
        StartResetPasswordCommand cmd = new StartResetPasswordCommand(loginRequest.getUsername());
        startResetPasswordHandler.handle(cmd);
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/complete-reset-password", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<Void>> completeResetPassword(@Valid @RequestBody CompleteResetPasswordRequest completeResetPasswordRequest) {
        CompleteResetPasswordCommand cmd = new CompleteResetPasswordCommand(
                completeResetPasswordRequest.getUsername(),
                completeResetPasswordRequest.getToken(),
                completeResetPasswordRequest.getNew_password()
        );
        ResultMap<Void> res = completeResetPasswordHandler.handle(cmd);
        return commandConverter.toResponse(res);
    }
}
