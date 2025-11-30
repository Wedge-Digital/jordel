package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountRequest;
import com.bloodbowlclub.JsonAssertions;
import com.bloodbowlclub.lib.services.result.exceptions.AlreadyExist;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;

@SpringBootTest(classes = WebApplication.class)
public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    @Transactional
    void test_regsiter_account_with_valid_data_succeeds() throws IOException {
        RegisterAccountRequest request = RegisterAccountRequest.builder()
                        .password("mon_gros_password")
                        .email("bertrand.begouin@gmail.com")
                        .username("Bagouze2556")
                        .build();

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    @Transactional
    @DisplayName("When try to register two times the same user, should fail")
    void test_two_times() {
        RegisterAccountRequest request = RegisterAccountRequest.builder()
                .password("mon_gros_password")
                .email("bertrand.begouin@gmail.com")
                .username("Bagouze2556")
                .build();

        ResponseEntity<Void> response = authController.registerUser(request);
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        try {
            authController.registerUser(request);
        } catch (AlreadyExist e) {
            HashMap<String,String> expectedErrors = new HashMap<>();
            expectedErrors.put("username", "Le nom d'utilisateur Bagouze2556 est déjà attribué, merci de choisir un autre");
            Assertions.assertEquals(expectedErrors, e.getErrors());
        }
    }
}
