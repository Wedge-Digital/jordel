package com.bloodbowlclub.auth.use_cases.policies;


import com.bloodbowlclub.WebApplication;
import com.bloodbowlclub.auth.domain.user_account.ActiveUserAccount;
import com.bloodbowlclub.auth.domain.user_account.DraftUserAccount;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntity;
import com.bloodbowlclub.lib.persistance.read_cache.ReadEntityType;
import com.bloodbowlclub.lib.persistance.read_cache.ReadRepository;
import com.bloodbowlclub.lib.services.ResultMap;
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
@Import(EmailShallNotExistPolicy.class)
public class EmailShallNotExistPolicyTest {

    @Autowired
    private ReadRepository readRepository;

    @Autowired
    private EmailShallNotExistPolicy emailShallNotExistPolicy;

    void loadPredefinedData(String email) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                "01K7YW65AECNXFPTZKFB3281FJ",
                "my_username",
                email,
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    void loadExternalData(String email) {
        DraftUserAccount draftAccount = new DraftUserAccount(
                "01K7YW65AECNXFPTZKFB3281FJ",
                "my_username",
                email,
                "my_password",
                new Date());
        ActiveUserAccount account = new ActiveUserAccount(draftAccount, new Date());
        ReadEntity readEntity = new ReadEntity(account);
        readRepository.save(readEntity);
        Assertions.assertEquals(1, readRepository.findAll().size());
    }

    @Test
    void TestEmailShallNotExistPolicy_fails_when_email_already_exists() {
        String email = "username_#1@gmail.com";
        loadPredefinedData(email);
        ResultMap<?> emailCheck = emailShallNotExistPolicy.check(email);
        Assertions.assertTrue(emailCheck.isFailure());
    }

    @Test
    void TestEmailShallNotExistPolicy_succeed_when_email_does_not_exists() throws JsonProcessingException {
        String email = "username_#1@gmail.com";
        ResultMap<?> emailCheck = emailShallNotExistPolicy.check(email);
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void Test_email_shall_not_exist_policy_succeed_when_other_email_is_present() {
        String email = "username_#1@gmail.com";
        loadPredefinedData(email);
        ResultMap<?> emailCheck = emailShallNotExistPolicy.check("gerard@hotmail.com");
        Assertions.assertTrue(emailCheck.isSuccess());
    }

    @Test
    void test_email_shall_not_exist_policy_succeed_when_another_aggregate_is_present() {
        String email = "username_#1@gmail.com";
        loadExternalData(email);
        ResultMap<?> emailCheck = emailShallNotExistPolicy.check(email);
        Assertions.assertTrue(emailCheck.isSuccess());
    }
}
