package com.auth.tests;

import com.auth.db_model.User;
import com.auth.domain.Coach;
import com.auth.services.Result;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import com.auth.db_model.mappers.CoachFactory;


@SpringBootTest
@ActiveProfiles("test")
public class UserDomainTest {

    @Test
    void contextLoads() {
    }



    @Test
    void TestMapDbUserObjectToUserDomainObject() {
        User user = new User();
        user.setId("01E48SD97BMWHAW82D229T0C7K");
        user.setCoachName("testCoach");
        user.setEmail("test@example.com");
        user.setPassword("$2a$12$WwdWTE5g9Ni16nxK90mamu8asuYWLhWKhg6MV96999AIGFRsCN262");
        user.setIsActive(true);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setLastLogin(new Date());

        Result<Coach> coachCreationResult = CoachFactory.fromUserModel(user);
        Assertions.assertThat(coachCreationResult.isSuccess()).isTrue();
        Assertions.assertThat(coachCreationResult.getValue()).isNotNull();
    }
}
