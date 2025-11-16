package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.JsonAssertions;
import com.bloodbowlclub.auth.io.services.JwtService;
import com.bloodbowlclub.auth.io.web.login.LoginRequest;
import com.bloodbowlclub.lib.tests.SucceedCommandHandler;
import com.bloodbowlclub.lib.tests.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public class AuthControllerLoginTest extends TestCase {

    SucceedCommandHandler loginHandler = new SucceedCommandHandler();
    JwtService jwtService = new JwtService(
            "jwt_secret__lkjqlkjqlsdkjqsldfjqsldjkfqslkdfjqslfjqslkdjfqsldjkflqs,:,:.qnsdlikjqsldfjkqsdf",
            8000,
            90000,
            messageSource);

    AuthController ctrl = new AuthController(
            jwtService,
            messageSource,
            loginHandler,
            null
            );


    @Test
    @DisplayName("Check a successful login retrieves a couple of jwt token")
    void test_a_successful_login_retrieves_a_couple_of_jwt_token() throws IOException {
        this.jwtService.init();
        LoginRequest loginRequest = new LoginRequest(
                "ElPoyOLoco",
                "anyPwd"
        );

        ResponseEntity<?> resp = this.ctrl.login(loginRequest);
        Assertions.assertEquals(HttpStatus.OK, resp.getStatusCode());

        Assertions.assertNotNull(resp.getBody());
        Assertions.assertNotNull(((JwtTokensResponse)resp.getBody()).accessToken);
        Assertions.assertNotNull(((JwtTokensResponse)resp.getBody()).refreshToken);

    }
}
