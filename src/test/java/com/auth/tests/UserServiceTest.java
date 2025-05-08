package com.auth.tests;

import com.auth.models.CustomUser;
import com.auth.services.Result;
import com.auth.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void TestFindUserbyNameReturnFailureforUnknownUser() {
        Result<CustomUser> result = userService.findByUsername("admin");
        Assertions.assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void TestFindUserbyNameReturnSuccess() {
        Result<CustomUser> result = userService.findByUsername("user_one");
        Assertions.assertThat(result.isSuccess()).isTrue();
        Assertions.assertThat(result.getValue().getUsername()).isEqualTo("user_one");
    }
}
