package com.auth.use_cases.policies;


import com.WebApplication;
import com.auth.domain.user_account.ActiveUserAccount;
import com.auth.domain.user_account.DraftUserAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lib.persistance.read_cache.ReadEntity;
import com.lib.persistance.read_cache.ReadEntityType;
import com.lib.persistance.read_cache.ReadRepository;
import com.lib.services.ObjectMapperService;
import com.lib.services.ResultMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = WebApplication.class)
@Import(UsernameShallNotExistPolicy.class)
public class UsernameShallNotExistPolicyTest {

    private final ObjectMapperService mapperService = new ObjectMapperService();

    @Autowired
    private ReadRepository readRepository;

    @Autowired
    private UsernameShallNotExistPolicy usernameShallNotExistPolicy;

    void loadPredefinedData(String username) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                "01K7YW65AECNXFPTZKFB3281FJ",
                username,
                "bb@gmail.com",
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(ReadEntityType.USER_ACCOUNT, account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    void loadExternalData(String username) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                "01K7YW65AECNXFPTZKFB3281FJ",
                username,
                "bb@gmail.com",
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(ReadEntityType.TEAM, account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    @Test
    void TestUsernameShallNotExistPolicy_fails_when_username_already_exists() {
        String username = "username_#1";
        loadPredefinedData(username);
        ResultMap<Void> emailCheck = usernameShallNotExistPolicy.check(username);
        Assertions.assertTrue(emailCheck.isFailure());
    }

    @Test
    void TestUsernameShallNotExistPolicy_succeed_when_username_does_not_exists() throws JsonProcessingException {
        String username = "username_#1";
        ResultMap<Void> emailCheck = usernameShallNotExistPolicy.check(username);
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void Test_username_shall_not_exist_policy_succeed_when_other_username_is_present() {
        String email = "username_#1";
        loadPredefinedData(email);
        ResultMap<Void> emailCheck = usernameShallNotExistPolicy.check("gerard@hotmail.com");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void test_username_shall_not_exist_policy_succeed_when_another_aggregate_is_present() {
        String email = "username_#1@gmail.com";
        loadExternalData(email);
        ResultMap<Void> emailCheck = usernameShallNotExistPolicy.check(email);
        Assertions.assertTrue(emailCheck.isSuccess());
    }
}
