package com.bloodbowlclub.auth.io.web;

import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.io.web.requests.RegisterAccountRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = WebApplication.class)
public class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Test
    @Transactional
    void test_regsiter_account_with_valid_data_succeeds() {
        RegisterAccountRequest request = RegisterAccountRequest.builder()
                        .password("mon_gros_password")
                        .email("bertrand.begouin@gmail.com")
                        .username("Bagouze2556")
                        .build();

        ResponseEntity<?> response = authController.registerUser(request);
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }
}
